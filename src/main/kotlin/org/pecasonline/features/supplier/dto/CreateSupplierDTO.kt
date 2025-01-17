package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.br.CNPJ
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.dto.CreateAddressDTO
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import org.pecasonline.features.subscription.CreateSubscriptionDTO
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier

class CreateSupplierDTO(
    @field:NotNull(message = "O nome do fornecedor é obrigatório")
    @field:NotBlank(message = "O nome do fornecedor não pode ser vazio")
    @JsonAlias("empresa")
    val name: String? = null,
    val supplierOriginalLink: String? = null,

    @field:NotNull(message = "A razão social do fornecedor é obrigatório")
    @field:NotBlank(message = "A razão social do fornecedor não pode ser vazio")
    @JsonAlias("razaoSocial")
    val socialName: String? = null,

    @field:NotNull(message = "O CNPJ do fornecedor é obrigatório")
    @field:NotBlank(message = "O CNPJ do fornecedor não pode ser vazio")
    @field:CNPJ(message = "O CNPJ precisa ser válido.")
    val cnpj: String? = null,

    @JsonAlias("inscricao")
    val stateSubscription: String? = null,

    @field:NotNull(message = "O ID da descrição do fornecedor é obrigatório")
    @JsonAlias("idDescricao")
    val descriptionId: Int? = null,

    @field:NotNull(message = "O ID da marca do fornecedor é obrigatório")
    @JsonAlias("idMarca")
    val brandId: Int? = null,

    @field:NotNull(message = "A assinatura do fornecedor é obrigatória")
    @JsonAlias("assinatura")
    val subscription : CreateSubscriptionDTO? = null,

    @get:Valid @field:NotNull(message = "O contato do fornecedor é obrigatório")
    @JsonAlias("contato")
    val contact: CreateContactDTO? = null,

    @get:Valid @field:NotNull(message = "O endereço do fornecedor é obrigatório")
    @JsonAlias("endereco")
    val address: CreateAddressDTO? = null,
) {
    fun toSupplier(contact: Contact,
                   address: Address, description:
                   Description, brand: Brand,
    ) = Supplier(
        name = name!!,
        supplierOriginalLink = supplierOriginalLink,
        socialName = socialName!!,
        cnpj = cnpj!!,
        stateSubscription = stateSubscription!!,
        description = description,
        brand = brand,
        address = address,
        contact = contact
    )
}