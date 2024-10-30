package org.pecasonline.features.supplier.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("vendedores")
    val sellerName: String,

    @JsonProperty("email")
    val itemsEmail: String,

    @JsonProperty("telefone")
    val itemsPhone: String,

    @JsonProperty("whatsapp")
    val whatsapp: String? = null,

    @JsonProperty("whatsappPecas")
    val itemsWhatsapp: String? = null,

    @JsonProperty("emailEstoque")
    val stockEmail: String? = null,

    @JsonProperty("emailContasPagar")
    val billingEmail: String? = null,

    @JsonProperty("emailNotaFiscal")
    val nfEmail: String? = null,

    @JsonProperty("webSite")
    val site: String? = null,
)