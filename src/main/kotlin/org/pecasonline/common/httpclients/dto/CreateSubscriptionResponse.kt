package org.pecasonline.common.httpclients.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class CreateSubscriptionResponse(
    @JsonProperty("object")
    val subscriptionObject: String,
    val id: String,
    val status: String,
    val customer: String,
    val billingType: String,
    val nextDueDate: String,
    val value: Double,
    val cycle: String,
    val description: String? = null,
    val endDate: String? = null,
    val maxPayments: Int? = null,
    val discount: DiscountResponse? = null,
    val fine: FineResponse? = null,
    val interest: InterestResponse? = null,
    val dateCreated: String,
    val paymentLink: String? = null
)