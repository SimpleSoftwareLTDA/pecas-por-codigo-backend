package org.pecasonline.common.httpclients.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateClientResponse(
    @JsonProperty("object")
    val customerObject: String,
    val id: String,
    val dateCreated: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val mobilePhone: String? = null,
    val cpfCnpj: String? = null,
    val postalCode: String? = null,
    val address: String? = null,
    val addressNumber: String? = null,
    val complement: String? = null,
    val province: String? = null,
    val externalReference: String? = null,
    val notificationDisabled: Boolean? = null,
    val additionalEmails: String? = null,
    val municipalInscription: String? = null,
    val stateInscription: String? = null,
    val observations: String? = null
)