package org.pecasonline.features.items

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
    val manufacturer: String? = null,
    val code: String,
    val priceInCents: Long? = null,
    val description: String? = null,
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