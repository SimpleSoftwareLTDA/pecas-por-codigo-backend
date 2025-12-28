package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.Valid
import jakarta.validation.constraints.Size
import org.pecasonline.features.address.dto.UpdateAddressDTO

class UpdateSupplierDTO(

    @JsonAlias("empresa", "name", "nome")
    @field:Size(max = 255, message = "O nome do fornecedor deve ter no máximo 255 caracteres")
    val name: String? = null,

    @JsonAlias("razaoSocial", "socialName")
    @field:Size(max = 255, message = "A razão social deve ter no máximo 255 caracteres")
    val socialName: String? = null,

    @JsonAlias("idDescricao")
    val descriptionId: Int? = null,

    @get:Valid
    @JsonAlias("contato")
    val contact: UpdateContactDTO? = null,

    @get:Valid
    @JsonAlias("endereco")
    val address: UpdateAddressDTO? = null,
)
