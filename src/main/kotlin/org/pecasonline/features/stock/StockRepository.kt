package org.pecasonline.features.stock

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int> {
    fun findStockByItemDescriptionContainsIgnoreCase(description: String, pageable: Pageable): Page<Stock>
    fun findStockByItemId(id: Int, pageable: Pageable): Page<Stock>
    fun findStockByItemCode(code: String, pageable: Pageable): Page<Stock>
    fun findByItemCode(code: String, pageable: Pageable): Page<Stock>
    fun findStockBySupplierId(id: Int, pageable: Pageable): Page<Stock>
    fun findStockBySupplierNameContainsIgnoreCase(name: String, pageable: Pageable): Page<Stock>
    fun findStockBySupplierIdAndItemId(supplierId: Int, itemId: Int): List<Stock>

    @Query(
        value = """
        SELECT s FROM Stock s 
        WHERE s.supplier.id = :supplierId
    """
    )
    fun findStocksBySupplierId(@Param("supplierId") supplierId: Int): List<Stock>


}