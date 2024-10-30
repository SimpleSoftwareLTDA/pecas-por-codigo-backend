package org.pecasonline.features.plan

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Plan (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String,
    val priceInCents: Long,
    val stock: Int,
    val quote: Int,
    val smallBanner: Boolean,
    val bigBanner: Boolean
)