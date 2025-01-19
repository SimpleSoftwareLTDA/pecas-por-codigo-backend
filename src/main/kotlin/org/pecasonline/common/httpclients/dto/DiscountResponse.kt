package org.pecasonline.common.httpclients.dto

data class DiscountResponse(
    val value: Double,
    val dueDateLimitDays: Int
)