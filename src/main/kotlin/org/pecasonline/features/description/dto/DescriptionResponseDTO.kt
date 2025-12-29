package org.pecasonline.features.description.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.description.Description

data class DescriptionResponseDTO(
    val id: Int?,
    @JsonProperty("descricao")
    val description: String
) {
    companion object {
        fun fromEntity(description: Description) = DescriptionResponseDTO(
            id = description.id,
            description = description.description
        )
    }
}
