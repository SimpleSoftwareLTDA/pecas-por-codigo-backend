package org.pecasonline.features.supplier.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val sellerName: String,
    val itemsEmail: String,
    val itemsPhone: String,
    val whatsapp: String? = null,
    val itemsWhatsapp: String? = null,
    val stockEmail: String? = null,
    val billingEmail: String? = null,
    val nfEmail: String? = null,
    val site: String? = null,
)