package org.pecasonline.features.supplier.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.pecasonline.features.supplier.domain.Contact

data class ContactResponseDTO(
    val id: Int?,
    @JsonProperty("vendedores")
    val sellerName: String,
    @JsonProperty("email")
    val itemsEmail: String,
    @JsonProperty("telefone")
    val itemsPhone: String,
    @JsonProperty("whatsapp")
    val whatsapp: String?,
    @JsonProperty("whatsappPecas")
    val itemsWhatsapp: String?,
    @JsonProperty("emailEstoque")
    val stockEmail: String?,
    @JsonProperty("emailContasPagar")
    val billingEmail: String?,
    @JsonProperty("emailNotaFiscal")
    val nfEmail: String?,
    @JsonProperty("webSite")
    val site: String?
) {
    companion object {
        fun fromEntity(contact: Contact) = ContactResponseDTO(
            id = contact.id,
            sellerName = contact.sellerName,
            itemsEmail = contact.itemsEmail,
            itemsPhone = contact.itemsPhone,
            whatsapp = contact.whatsapp,
            itemsWhatsapp = contact.itemsWhatsapp,
            stockEmail = contact.stockEmail,
            billingEmail = contact.billingEmail,
            nfEmail = contact.nfEmail,
            site = contact.site
        )
    }
}
