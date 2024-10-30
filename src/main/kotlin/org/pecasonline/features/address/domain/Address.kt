package org.pecasonline.features.address.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("endereco")
    val street: String,

    @JsonProperty("cidade")
    val city: String,

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "state_id", nullable = false)
    @JsonProperty("estado")
    val state: BrazilianState,

    @JsonProperty("cep")
    val cep: String,

    @JsonProperty("pais")
    val country: String? = "Brasil",
)
