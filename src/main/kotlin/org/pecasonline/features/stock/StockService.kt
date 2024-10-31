package org.pecasonline.features.stock

import org.pecasonline.common.exceptions.NotFoundException
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
    private val supplierRepository: SupplierRepository
) : IStockService {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getAllStocks(page: Int?, size: Int?) = stockRepository
        .findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock {
        return stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }
    }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?): Page<Stock> {
        val pageRequest = PageRequest.of(page ?: 0, size ?: 10)
        return stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, pageRequest)
    }

    override fun findStockByItemId(id: Int, page: Int?, size: Int?): Page<Stock> {
        return stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))
    }

    override fun finStockByItemCode(code: String, page: Int?, size: Int?): Page<Stock> {
        val pageRequest = PageRequest.of(page ?: 0, size ?: 10)
        return stockRepository.findStockByItemCode(code, pageRequest)
    }

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> {
        return stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))
    }

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> {
        val pageRequest = PageRequest.of(page ?: 0, size ?: 10)
        return stockRepository.findStockBySupplierNameContains(name, pageRequest)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createStock(cnpj: String, file: MultipartFile): List<Long> {
        val tmpDir: Path = Paths.get("tmp")
        logger.info("Creating temporary directory: {}", tmpDir)

        try {
            Files.createDirectories(tmpDir)
            logger.info("Temporary directory created: {}", tmpDir)

            if (file.isEmpty)
                throw IllegalArgumentException("Arquivo vazio, por favor selecione um arquivo para upload")

            val tempFile = saveFileLocallyTemporarily(file, tmpDir)
            logger.info("File saved locally: {}", tempFile)

            val stockList = getFileValues(tempFile)
            logger.info("File parsed: {}", stockList)

            val updatedIds = mutableListOf<Long>()
            val total = stockList.size
            var count = 0

            stockList.forEach { stock ->
                logger.debug("Processing stock: {}", stock)

                val item = itemRepository.findByHash(stock.item.hash) ?: itemRepository.save(stock.item)
                logger.debug("Item saved: {}", item)

                val supplier = supplierRepository.findSupplierByCnpj(cnpj)
                if (supplier.isEmpty())
                    throw NotFoundException("Fornecedor não encontrado")
                logger.debug("Supplier found: {}", supplier)

                supplier.parallelStream().forEach {
                    val stockBySupplierAndItem = stockRepository.findStockBySupplierIdAndItemId(it.id!!, item.id!!)
                    logger.debug("Stock found: {}. Updating stock...", stockBySupplierAndItem)

                    if (stockBySupplierAndItem.isEmpty()) {
                        val newStock = stock.copy(item = item, supplier = it)
                        logger.debug("Stock not found. Creating new stock: {}", newStock)
                        val saved = stockRepository.save(newStock)
                        logger.debug("Stock saved: {}", saved)
                        updatedIds.add(saved.id!!)
                    } else {
                        stockBySupplierAndItem.forEach { stockItem ->
                            val updatedStock = stockItem.copy(quantity = stockItem.quantity)
                            logger.debug("Stock found. Updating stock quantity: {}", updatedStock)
                            val saved = stockRepository.save(updatedStock)
                            logger.debug("Stock updated: {}", saved)
                            updatedIds.add(saved.id!!)
                        }
                    }

                    logger.info("processed ${++count} of $total suppliers")
                }
            }

            cleanupTempFiles(tmpDir)
            logger.info("Temporary files cleaned up. updatedIds: {}", updatedIds)

            return updatedIds
        } catch (e: Exception) {
            cleanupTempFiles(tmpDir) // Ensure cleanup on error as well
            logger.error("Error processing file", e)
            MDC.clear()
            throw e
        }
    }

    private fun cleanupTempFiles(directory: Path) {
        try {
            Files.list(directory).forEach { file ->
                try {
                    file.deleteIfExists()
                } catch (ex: IOException) {
                    throw IOException("Failed to delete file: $file", ex)
                }
            }
        } catch (e: IOException) {
            throw IOException("Failed to list files in directory: $directory", e)
        }
    }

    private fun saveFileLocallyTemporarily(file: MultipartFile, tmpDir: Path): Path {
        val uniqueFileName = "${UUID.randomUUID()}_${file.originalFilename}"
        val tempFile = tmpDir.resolve(uniqueFileName)
        Files.copy(file.inputStream, tempFile)
        return tempFile
    }

    private fun getFileValues(tempFile: Path): List<Stock> {
        val stockList = mutableListOf<Stock>()
        Files.newBufferedReader(tempFile).use { reader ->
            reader.forEachLine { line ->
                val columns = line.split("\\s+".toRegex()).filter { it.isNotEmpty() }
                if (columns.size >= 4) {
                    val code = columns[0]
                    val quantity = columns[1].toIntOrNull() ?: 0
                    val doublePrice = columns[2].toDoubleOrNull() ?: 0.0
                    val priceInLongCents = (doublePrice * 100).toLong()
                    val description = columns[3]

                    val item = Item.buildFromMinimalProperties(code, priceInLongCents, description)
                    val stock = Stock(quantity = quantity, item = item)
                    stockList.add(stock)
                }
            }
        }

        return stockList
    }

}