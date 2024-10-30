package org.pecasonline.features.items

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.Date

@Entity
data class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val manufacturer: String,
    val code: String,
    val priceInCents: Long? = null,
    val description: String? = null,
    val updateDate: Date? = Date(),
    val hash: String
)