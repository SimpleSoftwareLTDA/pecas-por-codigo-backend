package org.pecasonline.features.supplier.repository

import org.pecasonline.features.supplier.domain.Supplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SupplierRepository : JpaRepository<Supplier, Int> {
    fun findSupplierByCnpj(cnpj: String, pageable: Pageable): Page<Supplier>
    fun findSuppliersByCnpj(cnpj: String): List<Supplier>

    fun findSupplierByCnpj(cnpj: String): Supplier?


    @Query("SELECT s.cnpj FROM supplier s JOIN s.contact c WHERE c.itemsEmail = :email")
    fun findSupplierCnpjByEmail(email: String): String?

    @Query("SELECT c.itemsEmail FROM supplier s JOIN s.contact c WHERE s.cnpj = :cnpj")
    fun findSupplierEmailByCnpj(cnpj: String): String?

    @Query("SELECT s.name FROM supplier s JOIN s.contact c WHERE c.itemsEmail = :email")
    fun findSupplierNameByEmail(email: String): String

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM supplier s WHERE s.asaasId = :asaasId")
    fun existsByAsaasId(asaasId: String): Boolean

    fun findByAsaasId(asaasId: String): Supplier?

    @Query("SELECT s FROM supplier s JOIN s.contact c WHERE c.itemsEmail = :email")
    fun findSupplierByEmail(email: String): Supplier?

    @Query(
        value = ("SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END " +
                "FROM supplier s " +
                "JOIN tokens t ON t.supplier_id = s.id " +
                "WHERE t.token = :token AND s.cnpj = :cnpj"), nativeQuery = true
    )
    fun isTokenAssociatedWithCnpj(@Param("cnpj") cnpj: String?, @Param("token") token: String?): Boolean

    @Query(
        value = """
        SELECT s.cnpj 
        FROM supplier s
        JOIN tokens t ON t.supplier_id = s.id
        WHERE t.token = :token
    """, nativeQuery = true)
    fun findCnpjByToken(@Param("token") token: String): String?

}