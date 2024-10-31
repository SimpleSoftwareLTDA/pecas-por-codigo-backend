package org.pecasonline.features.address.domain

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("endereco")
    @JsonAlias("street")
    val street: String,

    @JsonProperty("cidade")
    @JsonAlias("city")
    val city: String,

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "state_id", nullable = false)
    @JsonProperty("estado")
    @JsonAlias("state")
    val state: BrazilianState,

    @JsonProperty("cep")
    @JsonAlias("cep")
    val cep: String,

    @JsonProperty("pais")
    @JsonAlias("country")
    val country: String? = "Brasil",
)
