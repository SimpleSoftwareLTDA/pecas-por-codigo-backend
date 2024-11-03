package org.pecasonline.features.stock

import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.deleteIfExists

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository,
    private val categoryService: ICategoryService
) : IStockService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getAllStocks(page: Int?, size: Int?): Page<Stock> =
        stockRepository.findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock =
        stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemCode(code: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemCode(code, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

    @Transactional(rollbackFor = [Exception::class])
    override fun createStock(cnpj: String, file: MultipartFile) {
        val tmpDir = Paths.get("tmp")
        logger.info("Starting stock creation from file upload for supplier CNPJ: {}", cnpj)

        if (file.isEmpty) throw IllegalArgumentException("Arquivo vazio, por favor selecione um arquivo para upload")

        Files.createDirectories(tmpDir)
        logger.debug("Created temporary directory: {}", tmpDir)

        val tempFile = saveFileLocallyTemporarily(file, tmpDir)
        logger.debug("Saved file locally: {}", tempFile)

        val stockList = getFileValues(tempFile)
        logger.debug("Parsed stock entries: {}", stockList.size)

        val updatedIds = mutableListOf<Long>()
        val total = stockList.size
        var count = 0

        stockList.forEach { stock ->
            count++
            val item = processItem(stock.item)
            logger.debug("Processed item with ID: {}, Hash: {}", item.id, item.hash)

            val suppliers = getSuppliers(cnpj)
            if(suppliers.isEmpty()) throw NotFoundException("Fornecedor não encontrado para CNPJ: $cnpj")
            logger.debug("Found {} suppliers for CNPJ {}", suppliers.size, cnpj)

            suppliers.forEach { supplier ->
                val supplierTotal = suppliers.size
                var supplierCount = 0
                val existingStocks = stockRepository.findStockBySupplierIdAndItemId(supplier.id!!, item.id!!)
                logger.debug("Found {} existing stocks for supplier ID: {} and item ID: {}", existingStocks.size, supplier.id, item.id)

                if (existingStocks.isNotEmpty()) {
                    existingStocks.forEach { existingStock ->
                        val updatedStock = existingStock.copy(quantity = stock.quantity)
                        stockRepository.save(updatedStock)
                        updatedIds.add(updatedStock.id!!)
                        logger.debug("Updated stock ID: {} with new quantity: {}", updatedStock.id, updatedStock.quantity)
                    }
                } else {
                    val newStock = stock.copy(item = item, supplier = supplier)
                    val savedStock = stockRepository.save(newStock)
                    updatedIds.add(savedStock.id!!)
                    logger.debug("Saved new stock with ID: {}", savedStock.id)
                }
                logger.info("Stock creation progress: {}/{} suppliers of item: ${count}, {}/{} stocks", ++supplierCount, supplierTotal, count, total)
            }

        }

        cleanupTempFiles(tmpDir)
        logger.info("Stock creation completed. Updated stock IDs: {}", updatedIds.size)
    }

    fun processItem(item: Item): Item {
        val category = getOrCreateCategory(item.description)
        val itemWithCategory = item.copy(category = category)
        logger.debug("Assigning category {} to item with hash {}", category.name, item.hash)
        return itemRepository.findByHash(itemWithCategory.hash) ?: itemRepository.save(itemWithCategory)
    }

    private fun getSuppliers(cnpj: String) =
        supplierRepository.findSupplierByCnpj(cnpj).takeIf { it.isNotEmpty() }
            ?: throw NotFoundException("Fornecedor não encontrado para CNPJ: $cnpj")

    fun cleanupTempFiles(directory: Path) {
        Files.list(directory).forEach { file ->
            try {
                file.deleteIfExists()
                logger.debug("Deleted temporary file: {}", file)
            } catch (ex: IOException) {
                logger.error("Failed to delete file: {}", file, ex)
            }
        }
    }

    private fun saveFileLocallyTemporarily(file: MultipartFile, tmpDir: Path): Path {
        val tempFile = tmpDir.resolve("${UUID.randomUUID()}_${file.originalFilename}")
        Files.copy(file.inputStream, tempFile)
        return tempFile
    }

    private fun getFileValues(tempFile: Path): List<Stock> {
        return Files.newBufferedReader(tempFile).use { reader ->
            reader.lineSequence().mapNotNull { parseStockLine(it) }.toList()
        }
    }

    private fun parseStockLine(line: String): Stock? {
        val columns = line.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (columns.size < 4) return null

        val (code, quantityStr, priceStr, description) = columns
        val quantity = quantityStr.toIntOrNull() ?: 0
        val priceInCents = ((priceStr.toDoubleOrNull() ?: 0.0) * 100).toLong()
        val item = Item.buildFromMinimalProperties(code, priceInCents, description)
        return Stock(quantity = quantity, item = item)
    }

    private fun getOrCreateCategory(description: String?): Category {
        val categoryName = description?.split(" ")?.firstOrNull()?.replace("\"", "") ?: "Uncategorized"
        val formattedName = categoryName.replaceFirstChar { it.uppercaseChar() } + categoryName.substring(1).lowercase()
        val category = categoryService.findByNameIgnoreCase(formattedName) ?: categoryService.addCategory(Category(name = formattedName))
        logger.debug("Retrieved or created category with name: {}", category.name)
        return category
    }
}
