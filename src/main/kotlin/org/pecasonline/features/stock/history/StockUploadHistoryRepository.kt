package org.pecasonline.features.stock.history

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import org.springframework.data.jpa.repository.EntityGraph

@Repository
interface StockUploadHistoryRepository : JpaRepository<StockUploadHistory, Long> {
    
    @EntityGraph(attributePaths = ["supplier"])
    fun findBySupplierId(supplierId: Int, pageable: Pageable): Page<StockUploadHistory>

    @EntityGraph(attributePaths = ["supplier"])
    override fun findAll(pageable: Pageable): Page<StockUploadHistory>
}
