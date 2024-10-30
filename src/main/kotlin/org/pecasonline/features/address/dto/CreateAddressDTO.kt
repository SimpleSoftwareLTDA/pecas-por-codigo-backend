package org.pecasonline.features.address.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotNull

data class CreateAddressDTO(
    @NotNull(message = "O campo 'endereco' é obrigatório")
    @JsonAlias("endereco")
    val street: String? = null,

    @NotNull(message = "O campo 'cidade' é obrigatório")
    @JsonAlias("cidade")
    val city: String? = null,

    @NotNull(message = "O campo 'cep' é obrigatório")
    @JsonAlias("cep")
    val cep: String? = null,

    @JsonAlias("pais")
    val country: String? = "Brasil",

    @NotNull(message = "O campo 'estado' é obrigatório")
    @JsonAlias("id_estado")
    val stateId: Int? = null,
)
