package org.pecasonline.features.brand.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.brand.Brand

data class BrandResponseDTO(
    val id: Int?,
    @JsonProperty("marca")
    val brandName: String
) {
    companion object {
        fun fromEntity(brand: Brand) = BrandResponseDTO(
            id = brand.id,
            brandName = brand.brandName
        )
    }
}
