package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import org.pecasonline.features.supplier.domain.Contact
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
    val description: Description?,
    @JsonProperty("marca")
    val brand: Brand?,
    val cnpj: String,
    @JsonProperty("inscricaoEstadual")
    val stateSubscription: String?,
    @JsonProperty("endereco")
    val address: Address,
    @JsonProperty("contato")
    val contact: Contact
) {
    companion object {
        fun fromEntity(supplier: Supplier) = SupplierResponseDTO(
            id = supplier.id,
            name = supplier.name,
            supplierOriginalLink = supplier.supplierOriginalLink,
            socialName = supplier.socialName,
            description = supplier.description,
            brand = supplier.brand,
            cnpj = supplier.cnpj,
            stateSubscription = supplier.stateSubscription,
            address = supplier.address,
            contact = supplier.contact
        )
    }
}
