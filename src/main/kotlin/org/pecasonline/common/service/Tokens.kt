package org.pecasonline.common.service

import jakarta.persistence.*
import jakarta.transaction.Transactional
import org.hibernate.Hibernate
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Entity
@Table(name = "tokens")
class Tokens {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "token_generator")
    @SequenceGenerator(name = "token_generator", sequenceName = "tokens_id_seq", allocationSize = 10)
    val id: Long? = null

    @Column(name = "created_at")
    lateinit var created: Instant
    lateinit var username: String
    lateinit var token: String

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Tokens

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

}

@Repository
interface TokenRepository : CrudRepository<Tokens, Long> {

    fun findByToken(token: String): Tokens?

    @Modifying
    @Transactional
    @Query("DELETE FROM Tokens t WHERE t.created < :threshold")
    fun deleteExpired(threshold: Instant)

}
