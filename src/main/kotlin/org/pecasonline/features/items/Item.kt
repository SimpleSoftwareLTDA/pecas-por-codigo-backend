package org.pecasonline.features.items

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.category.Category
import java.security.MessageDigest
import java.util.*

@Entity
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("fabricante")
    @JsonAlias(value = ["manufacturer"])
    val manufacturer: String? = null,

    @JsonProperty("codigo")
    @JsonAlias(value = ["code"])
    val code: String,

    @JsonProperty("precoEmCentavos")
    @JsonAlias(value = ["priceInCents", "price_in_cents"])
    val priceInCents: Long? = null,

    @JsonProperty("descricao")
    @JsonAlias(value = ["description"])
    val description: String? = null,

    @JsonProperty("dataDeAtualizacao")
    @JsonAlias(value = ["updateDate", "update_date"])
    val updateDate: Date? = Date(),
    val hash: String,

    @JsonProperty("categoria")
    @JsonAlias(value = ["category"])
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    val category: Category? = null

) {

    private fun hash(): String {
        val dataToHash = "$manufacturer$code$priceInCents$description"
        val md = MessageDigest.getInstance("MD5")
        val hashBytes = md.digest(dataToHash.toByteArray())

        val hash = hashBytes.joinToString("") { "%02x".format(it) }
        return hash
    }

    companion object {
        fun buildFromMinimalProperties(code: String, priceInCents: Long?, description: String?): Item {
            val item = Item(code = code, priceInCents = priceInCents, description = description, hash = "")
            return item.copy(hash = item.hash())
        }
    }
}