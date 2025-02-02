package org.pecasonline.features.login.entities

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.pecasonline.features.supplier.domain.Supplier
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

    @OneToOne
    @JoinColumn(name = "supplier_id")
    var supplier: Supplier? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Tokens

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

}
