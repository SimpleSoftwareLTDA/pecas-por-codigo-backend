package org.pecasonline.features.login.repositories

import org.pecasonline.features.login.entities.Tokens
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Repository
interface TokenRepository : CrudRepository<Tokens, Long> {

    fun findByToken(token: String): Tokens?

    @Modifying
    @Transactional
    @Query("DELETE FROM Tokens t WHERE t.created < :threshold")
    fun deleteExpired(threshold: Instant)

    @Query("""
        SELECT s.cnpj
        FROM Tokens t
        JOIN t.supplier s
        WHERE t.token = :token
    """)
    fun findSupplierCnpjByToken(token: String): String?

}