package org.pecasonline.features.address.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.Size

data class UpdateAddressDTO(
    @JsonAlias("endereco")
    @field:Size(max = 255, message = "O endere\u00e7o deve ter no m\u00e1ximo 255 caracteres")
    val street: String? = null,

    @JsonAlias("cidade")
    @field:Size(max = 255, message = "A cidade deve ter no m\u00e1ximo 255 caracteres")
    val city: String? = null,

    @JsonAlias("cep")
    @field:Size(max = 20, message = "O CEP deve ter no m\u00e1ximo 20 caracteres")
    val cep: String? = null,

    @JsonAlias("pais")
    @field:Size(max = 255, message = "O pa\u00eds deve ter no m\u00e1ximo 255 caracteres")
    val country: String? = null,

    @JsonAlias("idEstado")
    val stateId: Int? = null,
)
