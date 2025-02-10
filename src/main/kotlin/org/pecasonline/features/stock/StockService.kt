package org.pecasonline.features.stock

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.Constants.DEFAULT_FILE_NAME
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.common.isInvalidColumnSize
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.email.receiver.RegexPatterns.whitespaceRegex
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.deleteIfExists
import kotlin.io.path.pathString
import kotlin.streams.asSequence

private val logger = KotlinLogging.logger {}

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository,
    private val categoryService: ICategoryService,
    private val emailSenderService: EmailSenderService,
    private val subscriptionService: SubscriptionService
) : IStockService {

    override fun getAllStocks(page: Int?, size: Int?): Page<Stock> =
        stockRepository.findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock =
        stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemCode(code: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findByItemCode(code, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

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

        token?.let {
            val supplierWithToken = supplierRepository.isTokenAssociatedWithCnpj(cnpj, token)

            if (!supplierWithToken) {
                val errorMessage = "Token inválido ou não associado ao fornecedor com CNPJ: $cnpj."
                logger.error { errorMessage }

                throw IllegalArgumentException(errorMessage)
            }
        }

        val supplier = supplierRepository.findSupplierByCnpj(cnpj)

        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)

        when {
            file.isEmpty -> {
                val errorMessage = "Arquivo vazio, por favor selecione um arquivo de estoque com dados para upload."

                emailSenderService.sendStockProcessingErrorNotification(
                    supplierEmail = emailAddress,
                    fileName = file.originalFilename ?: DEFAULT_FILE_NAME,
                    errorMessage = errorMessage
                )

                throw IllegalArgumentException(errorMessage)
            }

            else -> {
                logger.info { "Iniciando criação de estoque a partir do arquivo do fornecedor CNPJ: $cnpj" }

                supplier.run {
                    emailSenderService.sendStockProcessingStartNotification(
                        supplierEmail = supplier.contact.itemsEmail,
                        supplierName = "${supplier.name} - ${supplier.cnpj}",
                        fileName = file.originalFilename ?: DEFAULT_FILE_NAME
                    )
                }

                val tempDir = Files.createTempDirectory("pecas-")
                val tempFile = saveTempFile(file, tempDir)

                logger.info { "${"Saved $tempFile file locally at ${tempFile.pathString}"} " }

                val stockList = getFileValuesOptimized(tempFile)
                logger.info { "Parsed stock entries: ${stockList.size}" }

                val updatedIds = mutableListOf<Long>()

                stockList.forEach { stock ->
                    val item = processItem(stock.item)

                    val existingStocks = supplier.id?.let { stockRepository.findStocksBySupplierId(it) }

                    logger.info { "Item processado com ID: ${item.id}, Hash: ${item.hash}" }

                    supplier.run {
                        when {
                            existingStocks?.isEmpty() == true -> {
                                val newStock = stock.copy(item = item, supplier = supplier)
                                val savedStock = stockRepository.save(newStock)

                                updatedIds.add(savedStock.id!!)

                                logger.info { "Novo estoque salvo com ID: ${savedStock.id}" }
                            }

                            else -> {
                                existingStocks?.forEach { existingStock ->
                                    val updatedStock = existingStock.copy(quantity = stock.quantity)

                                    stockRepository.save(updatedStock)
                                    updatedIds.add(updatedStock.id!!)

                                    logger.info { "Estoque atualizado com ID: ${updatedStock.id}, Quantidade: ${updatedStock.quantity}" }
                                }
                            }
                        }
                    }
                }

                Files.deleteIfExists(tempFile)

                supplier.run {
                    emailSenderService.sendStockProcessingCompletionNotification(
                        supplierEmail = this.contact.itemsEmail,
                        supplierName = "${this.name} - ${this.cnpj}",
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

        logger.info { "Assigning category ${category.name} to item with hash ${item.hash}" }

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

                logger.info { "${"Deleted temporary file: $file"} " }
            }.onFailure { ex ->
                logger.error(ex) { "Failed to delete file: $file" }
            }
        }
    }

    private fun saveTempFile(file: MultipartFile, tmpDir: Path): Path =
        Files.createTempFile(tmpDir, "tmp_", "_${file.originalFilename ?: "unknown"}")
            .also { Files.copy(file.inputStream, it, StandardCopyOption.REPLACE_EXISTING) }


    private fun getFileValuesOptimized(tempFile: Path): List<Stock> =
        Files.lines(tempFile).use { lines ->
            lines.asSequence()
                .filterNot { it.isNullOrBlank() }
                .map { parseStockLine(it) }
                .filterNotNull()
                .map { it }
                .toList()
        }

    private fun parseStockLine(line: String): Stock? {
        val columns = line.trim().split(whitespaceRegex, 4).filter { it.isNotEmpty() }

        when {
            columns.isInvalidColumnSize() -> return null

            else -> {
                val (code, quantityStr, priceStr, description) = columns

                val quantity = quantityStr.toIntOrNull()?: 0
                val priceInCents = ((priceStr.toDoubleOrNull()?: 0.0) * 100).toLong()

                val item = Item.buildFromMinimalProperties(code, priceInCents, description)

                return Stock(quantity = quantity, item = item)
            }
        }
    }

    private fun getOrCreateCategory(description: String?): Category {
        val formattedName = description ?: "Uncategorized".replaceFirstChar { it.uppercaseChar() }.lowercase()
        val category = categoryService.findByNameIgnoreCase(formattedName) ?: categoryService.addCategory(Category(name = formattedName))

        logger.info { "${"Retrieved or created category with name: ${category.name}"} " }

        return category
    }
}
