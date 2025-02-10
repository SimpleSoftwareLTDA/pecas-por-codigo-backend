package org.pecasonline.features.stock

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.Constants.DEFAULT_FILE_NAME
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.deleteIfExists

private val logger = KotlinLogging.logger {}

@Service
class StockBatchService(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository,
    private val categoryService: ICategoryService,
    private val emailSenderService: EmailSenderService,
    private val subscriptionService: SubscriptionService
) : IStockService {

    override fun getAllStocks(page: Int?, size: Int?): Page<Stock> = stockRepository.findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock = stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?): Page<Stock> = stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemId(id: Int, page: Int?, size: Int?): Page<Stock> = stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemCode(code: String, page: Int?, size: Int?): Page<Stock> = stockRepository.findStockByItemCode(code, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> = stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> = stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

    @Transactional(rollbackFor = [Exception::class])
    override fun createStock(file: MultipartFile, emailAddress: String, token: String?) {
        val cnpj = token?.let {
            getSupplierByToken(token)
        } ?: supplierRepository.findSupplierCnpjByEmail(emailAddress)

        if (cnpj == null) {
            val errorMessage = "CNPJ não encontrado para o token ou email fornecido. Token: $token, Email: $emailAddress."
            logger.error { errorMessage }

            return
        }

        // Se o processamento do arquivo for iniciado via site web, o token deve ser válido.
        token?.let {
            val supplierWithToken = supplierRepository.isTokenAssociatedWithCnpj(cnpj, token)

            if (!supplierWithToken) {
                val errorMessage = "Token inválido ou não associado ao fornecedor com CNPJ: $cnpj."
                logger.error { errorMessage }

                throw IllegalArgumentException(errorMessage)
            }
        }

        val supplier = getSupplierByCNPJ(cnpj)

        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)

        when {
            file.isEmpty -> {
                val errorMessage = "Arquivo vazio, por favor selecione um arquivo de estoque com dados para upload."

                supplierRepository.findSupplierEmailByCnpj(cnpj)

                emailSenderService.sendStockProcessingErrorNotification(
                    supplierEmail = emailAddress,
                    fileName = file.originalFilename ?: DEFAULT_FILE_NAME,
                    errorMessage = errorMessage
                )

                throw IllegalArgumentException(errorMessage)
            }

            else -> {
                val tmpDir: Path = Files.createTempDirectory("pecas-")
                logger.info { "Iniciando criação de estoque a partir do arquivo do fornecedor CNPJ: $cnpj" }

                val allSuppliers = getSupplierByCNPJ(cnpj)

                allSuppliers.forEach { supplier ->
                    emailSenderService.sendStockProcessingStartNotification(
                        supplierEmail = supplier.contact.itemsEmail ?: emailAddress,
                        supplierName = "${supplier.name} - ${supplier.cnpj}",
                        fileName = file.originalFilename ?: DEFAULT_FILE_NAME
                    )
                }

                logger.debug { "${"Created temporary directory: {}"} $tmpDir" }

                val tempFile = saveFileLocallyTemporarily(file, tmpDir)
                logger.debug { "${"Saved file locally: {}"} $tempFile" }

                val stockList = getFileValuesOptimized(tempFile)
                logger.debug { "${"Parsed stock entries: {}"} ${stockList.size}" }

                val updatedIds = mutableListOf<Long>()

                stockList.forEach { stock ->
                    val item = processItem(stock.item)

                    logger.debug { "Item processado com ID: ${item.id}, Hash: ${item.hash}" }

                    val suppliers = getSupplierByCNPJ(cnpj)

                    if (suppliers.isEmpty()) {
                        val errorMessage = "Fornecedor não encontrado para CNPJ: $cnpj"
                        throw NotFoundException(errorMessage)
                    }

                    suppliers.forEach { supplier ->
                        val existingStocks = stockRepository.findStockBySupplierIdAndItemId(supplier.id!!, item.id!!)

                        when {
                            existingStocks.isEmpty() -> {
                                val newStock = stock.copy(item = item, supplier = supplier)
                                val savedStock = stockRepository.save(newStock)

                                updatedIds.add(savedStock.id!!)

                                logger.debug { "Novo estoque salvo com ID: ${savedStock.id}" }
                            }

                            else -> {
                                existingStocks.forEach { existingStock ->
                                    val updatedStock = existingStock.copy(quantity = stock.quantity)

                                    stockRepository.save(updatedStock)
                                    updatedIds.add(updatedStock.id!!)

                                    logger.debug { "Estoque atualizado com ID: ${updatedStock.id}, Quantidade: ${updatedStock.quantity}" }
                                }
                            }
                        }
                    }
                }

                cleanupTempFiles(tmpDir)
                // Notificação de conclusão do processamento
                allSuppliers.forEach {
                    emailSenderService.sendStockProcessingCompletionNotification(
                        supplierEmail = it.contact.itemsEmail ?: emailAddress,
                        supplierName = "${it.name} - ${it.cnpj}",
                        fileName = file.originalFilename ?: DEFAULT_FILE_NAME,
                        updatedItemCount = updatedIds.size
                    )
                }

                logger.info { "Criação de estoque finalizada. IDs de estoque atualizados: ${updatedIds.size}" }
            }
        }
    }

    fun processItem(item: Item): Item {
        val category = getOrCreateCategory(item.description)
        val itemWithCategory = item.copy(category = category)

        logger.debug { "Assigning category ${category.name} to item with hash ${item.hash}" }

        return itemRepository.findByHash(itemWithCategory.hash) ?: itemRepository.save(itemWithCategory)
    }

    private fun getSupplierByCNPJ(cnpj: String) =
        supplierRepository.findSuppliersByCnpj(cnpj)
            .takeIf { it.isNotEmpty() }
            ?: throw NotFoundException("Fornecedor não encontrado para o CNPJ: $cnpj. Faça sua assinatura para usar esse serviço.")

    private fun getSupplierByToken(token: String): String =
        supplierRepository.findCnpjByToken(token)
            ?: throw NotFoundException("Fornecedor não encontrado a partir desse token")

    fun cleanupTempFiles(directory: Path) {
        Files.list(directory).forEach { file ->
            runCatching {
                file.deleteIfExists()

                logger.debug { "${"Deleted temporary file: $file"} " }
            }.onFailure { ex ->
                logger.error(ex) { "Failed to delete file: $file" }
            }
        }
    }

    private fun saveFileLocallyTemporarily(file: MultipartFile, tmpDir: Path): Path {
        val tempFile = Files.createTempFile(tmpDir, "tmp_", "_${file.originalFilename}")

        file.inputStream.use { inputStream ->
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING)
        }

        return tempFile
    }


    private fun getFileValues(tempFile: Path): List<Stock> =
        Files.newBufferedReader(tempFile).use { reader ->
            reader.lineSequence().mapNotNull { parseStockLine(it) }.toList()
        }

    private fun getFileValuesOptimized(tempFile: Path): List<Stock> =
        Files.lines(tempFile).use { lines ->
            lines.parallel()
                .map { parseStockLine(it) }
                .filter { it != null }
                .map { it!! }
                .toList()
        }

    private fun parseStockLine(line: String): Stock? {
        val columns = line.split("\\s+".toRegex()).filter { it.isNotEmpty() }

        when {
            columns.size < 4 -> return null

            else -> {
                val (code, quantityStr, priceStr, description) = columns

                val quantity = quantityStr.toIntOrNull() ?: 0
                val priceInCents = ((priceStr.toDoubleOrNull() ?: 0.0) * 100).toLong()

                val item = Item.buildFromMinimalProperties(code, priceInCents, description)

                return Stock(quantity = quantity, item = item)
            }
        }
    }

    private fun getOrCreateCategory(description: String?): Category {
        val categoryName = description?.split(" ")?.firstOrNull()?.replace("\"", "") ?: "Uncategorized"
        val formattedName = categoryName.replaceFirstChar { it.uppercaseChar() }.lowercase()
        val category = categoryService.findByNameIgnoreCase(formattedName)
            ?: categoryService.addCategory(Category(name = formattedName))

        logger.debug { "${"Retrieved or created category with name: ${category.name}"} " }

        return category
    }

    private fun processFileDirectly(inputStream: InputStream): List<Stock> =
        inputStream.bufferedReader().use { reader ->
            reader.lineSequence()
                .mapNotNull { parseStockLine(it) }
                .toList()
        }

}
