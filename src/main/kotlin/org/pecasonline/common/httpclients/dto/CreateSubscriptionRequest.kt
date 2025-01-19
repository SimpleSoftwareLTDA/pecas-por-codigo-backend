package org.pecasonline.common.httpclients.dto

data class CreateSubscriptionRequest(
    val customer: String,
    val billingType: String,
    val nextDueDate: String,
    val value: Double,
    val cycle: String,
    val description: String? = null,
    val endDate: String? = null,
    val maxPayments: Int? = null,
    val discount: Discount? = null,
    val fine: Fine? = null,
    val interest: Interest? = null
)