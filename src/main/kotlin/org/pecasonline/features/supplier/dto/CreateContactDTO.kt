package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.pecasonline.features.supplier.domain.Contact

data class CreateContactDTO(
    @NotNull(message = "O nome do vendedores é obrigatório")
    @NotBlank(message = "O nome do vendedores não pode ser vazio")
    @JsonAlias("vendedores")
    val sellerName: String? = null,

    @NotNull(message = "O email de peças é obrigatório")
    @NotBlank(message = "O email de peças não pode ser vazio")
    @JsonAlias("emailPecas")
    val itemsEmail: String? = null,

    @NotNull(message = "O telefone de peças é obrigatório")
    @NotBlank(message = "O telefone de peças não pode ser vazio")
    @JsonAlias("fonePecas")
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
) {
    fun toContact() = Contact(
        sellerName = sellerName!!,
        itemsEmail = itemsEmail!!,
        itemsPhone = itemsPhone!!,
        whatsapp = whatsapp,
        itemsWhatsapp = itemsWhatsapp,
        stockEmail = stockEmail,
        billingEmail = billingEmail,
        nfEmail = nfEmail,
        site = site
    )
}