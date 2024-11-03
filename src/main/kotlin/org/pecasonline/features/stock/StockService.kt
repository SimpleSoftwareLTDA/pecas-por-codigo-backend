package org.pecasonline.features.stock

import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
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

    override fun getAllStocks(page: Int?, size: Int?) =
        stockRepository.findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock =
        stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?) =
        stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemId(id: Int, page: Int?, size: Int?) =
        stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun finStockByItemCode(code: String, page: Int?, size: Int?) =
        stockRepository.findStockByItemCode(code, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?) =
        stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?) =
        stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

    @Transactional(rollbackFor = [Exception::class])
    override fun createStock(cnpj: String, file: MultipartFile) {
        val tmpDir = Paths.get("tmp")
        logger.info("Starting stock creation from file upload.")

        if (file.isEmpty) throw IllegalArgumentException("Arquivo vazio, por favor selecione um arquivo para upload")

        Files.createDirectories(tmpDir)
        val tempFile = saveFileLocallyTemporarily(file, tmpDir)
        val stockList = getFileValues(tempFile)
        val updatedIds = mutableListOf<Long>()

        stockList.forEach { stock ->
            val item = processItem(stock.item)
            val suppliers = getSuppliers(cnpj)

            suppliers.forEach { supplier ->
                val existingStock = stockRepository.findStockBySupplierIdAndItemId(supplier.id!!, item.id!!)
                val stockToSave = if (existingStock.isEmpty()) stock.copy(item = item, supplier = supplier) else updateExistingStock(existingStock)
                updatedIds.add(stockRepository.save(stockToSave).id!!)
            }
        }

        cleanupTempFiles(tmpDir)
        logger.info("Stock creation completed. Updated IDs: {}", updatedIds)
    }

    fun processItem(item: Item): Item {
        val category = getOrCreateCategory(item.description)
        val itemWithCategory = item.copy(category = category)
        return itemRepository.findByHash(itemWithCategory.hash) ?: itemRepository.save(itemWithCategory)
    }

    private fun getSuppliers(cnpj: String) =
        supplierRepository.findSupplierByCnpj(cnpj).takeIf { it.isNotEmpty() } ?: throw NotFoundException("Fornecedor não encontrado")

    private fun updateExistingStock(existingStocks: List<Stock>): Stock {
        val existingStock = existingStocks.first()
        return existingStock.copy(quantity = existingStock.quantity)
    }

    fun cleanupTempFiles(directory: Path) {
        Files.list(directory).forEach { file ->
            try { file.deleteIfExists() }
            catch (ex: IOException) { logger.error("Failed to delete file: {}", file, ex) }
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
        val priceInCents = ((priceStr.toDoubleOrNull() ?: (0.0 * 100))).toLong()
        val item = Item.buildFromMinimalProperties(code, priceInCents, description)
        return Stock(quantity = quantity, item = item)
    }

    fun getOrCreateCategory(description: String?): Category {
        val categoryName = description?.split(" ")?.firstOrNull()?.replace("\"", "") ?: "Uncategorized"
        val formattedName = categoryName.replaceFirstChar { it.uppercaseChar() } + categoryName.substring(1).lowercase()
        return categoryService.findByNameIgnoreCase(formattedName) ?: categoryService.addCategory(Category(name = formattedName))
    }
}
