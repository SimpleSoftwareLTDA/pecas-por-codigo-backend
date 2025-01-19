package org.pecasonline.common.httpclients.dto

data class CreateClientRequest(
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