package org.pecasonline.features.address.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.address.domain.Address

data class AddressResponseDTO(
    val id: Int?,
    @JsonProperty("endereco")
    val street: String,
    @JsonProperty("cidade")
    val city: String,
    @JsonProperty("estado")
    val state: StateResponseDTO,
    @JsonProperty("cep")
    val cep: String,
    @JsonProperty("pais")
    val country: String?
) {
    companion object {
        fun fromEntity(address: Address) = AddressResponseDTO(
            id = address.id,
            street = address.street,
            city = address.city,
            state = StateResponseDTO.fromEntity(address.state),
            cep = address.cep,
            country = address.country
        )
    }
}
