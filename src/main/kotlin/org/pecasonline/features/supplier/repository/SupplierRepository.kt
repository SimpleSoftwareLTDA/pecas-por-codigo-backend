package org.pecasonline.features.supplier.repository

import org.pecasonline.features.supplier.domain.Supplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : JpaRepository<Supplier, Int> {
    fun findSupplierByCnpj(cnpj: String, pageable: Pageable): Page<Supplier>
    fun findSupplierByCnpj(cnpj: String): List<Supplier>


    @Query("SELECT s.cnpj FROM supplier s JOIN s.contact c WHERE c.itemsEmail = :email")
    fun findSupplierCnpjByEmail(email: String): String?
}