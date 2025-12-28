package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.Size

data class UpdateContactDTO(
    @JsonAlias("vendedores")
    @field:Size(max = 255, message = "O nome dos vendedores deve ter no m\u00e1ximo 255 caracteres")
    val sellerName: String? = null,

    @JsonAlias("emailPecas", "email")
    @field:Size(max = 255, message = "O email de pe\u00e7as deve ter no m\u00e1ximo 255 caracteres")
    val itemsEmail: String? = null,

    @JsonAlias("fonePecas")
    @field:Size(max = 50, message = "O telefone deve ter no m\u00e1ximo 50 caracteres")
    val itemsPhone: String? = null,

    @JsonAlias("whatsappGeral")
    val whatsapp: String? = null,

    @JsonAlias("whatsappPecas")
    val itemsWhatsapp: String? = null,

    @JsonAlias("emailEstoque")
    val stockEmail: String? = null,

    @JsonAlias("emailContasPagar")
    val billingEmail: String? = null,

    @JsonAlias("emailNotaFiscal")
    val nfEmail: String? = null,

    @JsonAlias("website")
    val site: String? = null,
)
