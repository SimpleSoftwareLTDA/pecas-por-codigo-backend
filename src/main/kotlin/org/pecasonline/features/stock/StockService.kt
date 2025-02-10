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

        if (cnpj == null) error("CNPJ não encontrado para o token ou email fornecido.")

        token?.let {
            if (!supplierRepository.isTokenAssociatedWithCnpj(cnpj, token)) error("Token inválido ou não associado ao fornecedor com CNPJ: $cnpj.")
        }

        val supplier = supplierRepository.findSupplierByCnpj(cnpj)
        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)

        when {
            file.isEmpty -> {
                val errorMessage = "Arquivo vazio. Selecione um arquivo de estoque com dados para upload."
                emailSenderService.sendStockProcessingErrorNotification(
                    supplierEmail = emailAddress,
                    fileName = file.originalFilename ?: DEFAULT_FILE_NAME,
                    errorMessage = errorMessage
                )
                throw IllegalArgumentException(errorMessage)
            }

            else -> {
                logger.info { "Iniciando atualização de estoque para o fornecedor CNPJ: $cnpj" }

                emailSenderService.sendStockProcessingStartNotification(
                    supplierEmail = supplier.contact.itemsEmail,
                    supplierName = "${supplier.name} - ${supplier.cnpj}",
                    fileName = file.originalFilename ?: DEFAULT_FILE_NAME
                )

                val tempDir = Files.createTempDirectory("pecas-")
                val tempFile = saveTempFile(file, tempDir)

                try {
                    val stockList = getFileValuesOptimized(tempFile)
                    logger.info { "Total de itens processados no arquivo: ${stockList.size}" }

                    val existingStocks = stockRepository.findStocksBySupplierId(supplier.id!!)
                    val existingStocksMap = existingStocks.associateBy { it.item.hash }

                    val updatedIds = mutableSetOf<Long>()
                    val newStocks = mutableListOf<Stock>()

                    stockList.forEach { stock ->
                        val item = processItem(stock.item)

                        val existingStock = existingStocksMap[item.hash]

                        if (existingStock != null) {
                            val updatedStock = existingStock.copy(quantity = stock.quantity)
                            stockRepository.save(updatedStock)
                            updatedIds.add(updatedStock.id!!)

                            logger.info { "Estoque atualizado: ID=${updatedStock.id}, Quantidade=${updatedStock.quantity} Código=${updatedStock.item.code}" }
                        } else {
                            newStocks.add(stock.copy(item = item, supplier = supplier))

                            logger.info { "Novo item adicionado ao estoque: Item=${item.hash}" }
                        }
                    }

                    if (newStocks.isNotEmpty()) {
                        stockRepository.saveAll(newStocks)
                        updatedIds.addAll(newStocks.map { it.id!! })
                    }

                    emailSenderService.sendStockProcessingCompletionNotification(
                        supplierEmail = supplier.contact.itemsEmail,
                        supplierName = "${supplier.name} - ${supplier.cnpj}",
                        fileName = file.originalFilename ?: DEFAULT_FILE_NAME,
                        updatedItemCount = updatedIds.size
                    )

                    logger.info { "Atualização de estoque concluída para o fornecedor CNPJ: $cnpj" }

                } finally {
                    Files.deleteIfExists(tempFile)
                    logger.info { "Arquivo temporário removido: ${tempFile.pathString}" }
                }
            }
        }
    }

    fun processItem(item: Item): Item {
        val category = getOrCreateCategory(item.description)
        val itemWithCategory = item.copy(category = category)

        logger.info { "Assigning category ${category.name} to item with hash ${item.hash}" }

        return itemRepository.findByHash(itemWithCategory.hash) ?: itemRepository.save(itemWithCategory)
    }

    private fun getSupplierByToken(token: String): String =
        supplierRepository.findCnpjByToken(token)
            ?: throw NotFoundException("Fornecedor não encontrado a partir desse token")

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
