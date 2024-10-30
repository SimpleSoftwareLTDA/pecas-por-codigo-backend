package org.pecasonline.features.plan

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Plan (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("nome")
    val name: String,

    @JsonProperty("precoEmCentavos")
    val priceInCents: Long,

    @JsonProperty("estoque")
    val stock: Boolean,

    @JsonProperty("descricao")
    val quote: Boolean,

    @JsonProperty("bannerPequeno")
    val smallBanner: Boolean,

    @JsonProperty("bannerGrande")
    val bigBanner: Boolean
)