package org.pecasonline.features.items

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.security.MessageDigest
import java.util.Date

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
    val hash: String
) {

    private fun hash(): String {
        val dataToHash = "$manufacturer$code$priceInCents$description"
        val md = MessageDigest.getInstance("MD5")
        val hashBytes = md.digest(dataToHash.toByteArray())

        // Convert the byte array to a hex string
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