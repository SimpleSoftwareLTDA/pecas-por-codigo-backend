package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.address.dto.AddressResponseDTO
import org.pecasonline.features.brand.dto.BrandResponseDTO
import org.pecasonline.features.description.dto.DescriptionResponseDTO
import org.pecasonline.features.supplier.domain.Supplier

data class SupplierResponseDTO(
    val id: Int?,
    @JsonProperty("nome")
    val name: String,
    @JsonProperty("linkFornecedorOriginal")
    val supplierOriginalLink: String?,
    @JsonProperty("razaoSocial")
    val socialName: String,
    @JsonProperty("descricao")
    val description: DescriptionResponseDTO?,
    @JsonProperty("marca")
    val brand: BrandResponseDTO?,
    val cnpj: String,
    @JsonProperty("inscricaoEstadual")
    val stateSubscription: String?,
    @JsonProperty("endereco")
    val address: AddressResponseDTO,
    @JsonProperty("contato")
    val contact: ContactResponseDTO
) {
    companion object {
        fun fromEntity(supplier: Supplier) = SupplierResponseDTO(
            id = supplier.id,
            name = supplier.name,
            supplierOriginalLink = supplier.supplierOriginalLink,
            socialName = supplier.socialName,
            description = supplier.description?.let { DescriptionResponseDTO.fromEntity(it) },
            brand = supplier.brand?.let { BrandResponseDTO.fromEntity(it) },
            cnpj = supplier.cnpj,
            stateSubscription = supplier.stateSubscription,
            address = AddressResponseDTO.fromEntity(supplier.address),
            contact = ContactResponseDTO.fromEntity(supplier.contact)
        )
    }
}
