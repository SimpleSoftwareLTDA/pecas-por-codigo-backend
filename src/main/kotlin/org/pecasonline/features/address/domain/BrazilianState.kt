package org.pecasonline.features.address.domain

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
@Table(name = "brazilian_state")
data class BrazilianState(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("sigla")
    @JsonAlias(value = ["state_code", "stateCode"])
    val stateCode: String,

    @JsonProperty("nome")
    @JsonAlias(value = ["state_name", "stateName"])
    val stateName: String
)