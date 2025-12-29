package org.pecasonline.features.address.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.address.domain.BrazilianState

data class StateResponseDTO(
    val id: Int?,
    @JsonProperty("sigla")
    val stateCode: String,
    @JsonProperty("nome")
    val stateName: String
) {
    companion object {
        fun fromEntity(state: BrazilianState) = StateResponseDTO(
            id = state.id,
            stateCode = state.stateCode,
            stateName = state.stateName
        )
    }
}
