package org.pecasonline.features.address.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotNull

data class CreateAddressDTO(
    @field:NotNull(message = "O campo 'endereco' é obrigatório")
    @JsonAlias("endereco")
    val street: String? = null,

    @field:NotNull(message = "O campo 'cidade' é obrigatório")
    @JsonAlias("cidade")
    val city: String? = null,

    @field:NotNull(message = "O campo 'cep' é obrigatório")
    @JsonAlias("cep")
    val cep: String? = null,

    @field:JsonAlias("pais")
    val country: String? = "Brasil",

    @field:NotNull(message = "O campo 'idEstado' é obrigatório")
    @JsonAlias("idEstado")
    val stateId: Int? = null,
)
