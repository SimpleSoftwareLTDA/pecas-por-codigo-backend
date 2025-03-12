package org.pecasonline.features.stock

import org.springframework.data.domain.Page
import java.io.File

interface IStockService {
    fun getAllStocks(page: Int? = 0, size: Int? = 10): Page<Stock>
    fun findStockById(id: Int): Stock
    fun findStockByItemDescription(description: String, page: Int? = 0, size: Int? = 10): Page<Stock>
    fun findStockByItemId(id: Int, page: Int? = 0, size: Int? = 10): Page<Stock>
    fun findStockByItemCode(code: String, page: Int? = 0, size: Int? = 10): Page<Stock>
    fun findStockBySupplierId(id: Int, page: Int? = 0, size: Int? = 10): Page<Stock>
    fun findStockBySupplierName(name: String, page: Int? = 0, size: Int? = 10): Page<Stock>
    fun createStock(file: File, emailAddress: String = "", token: String? = null)
}