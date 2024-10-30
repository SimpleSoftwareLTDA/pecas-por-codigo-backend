package org.pecasonline.features.stock

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int> {
    fun findStockByItemDescriptionContains(description: String, pageable: Pageable): Page<Stock>
    fun findStockByItemId(id: Int, pageable: Pageable): Page<Stock>
    fun findStockByItemCode(code: String, pageable: Pageable): Page<Stock>
    fun findStockBySupplierId(id: Int, pageable: Pageable): Page<Stock>
    fun findStockBySupplierNameContains(name: String, pageable: Pageable): Page<Stock>
    fun findStockBySupplierIdAndItemId(supplierId: Int, itemId: Int): List<Stock>
}