package org.pecasonline.features.plan

import com.fasterxml.jackson.annotation.JsonAlias
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
    @JsonAlias(value = ["name"])
    val name: String,

    @JsonProperty("precoEmCentavos")
    @JsonAlias(value = ["priceInCents", "price_in_cents"])
    val priceInCents: Long,

    @JsonProperty("estoque")
    @JsonAlias(value = ["stock"])
    val stock: Boolean,

    @JsonProperty("descricao")
    @JsonAlias(value = ["description"])
    val quote: Boolean,

    @JsonProperty("bannerPequeno")
    @JsonAlias(value = ["smallBanner", "small_banner"])
    val smallBanner: Boolean,

    @JsonProperty("bannerGrande")
    @JsonAlias(value = ["bigBanner", "big_banner"])
    val bigBanner: Boolean
)