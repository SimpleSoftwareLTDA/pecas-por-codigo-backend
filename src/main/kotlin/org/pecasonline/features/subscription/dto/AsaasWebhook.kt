package org.pecasonline.features.subscription.dto

data class AsaasWebhook(
    val id: String,
    val event: String,
    val dateCreated: String,
    val payment: Payment
)

data class Payment(
    val `object`: String,
    val id: String,
    val dateCreated: String,
    val customer: String,
    val subscription: String?,
    val paymentLink: String?,
    val value: Double,
    val netValue: Double,
    val originalValue: Double?,
    val interestValue: Double?,
    val description: String?,
    val billingType: String,
    val canBePaidAfterDueDate: Boolean,
    val pixTransaction: String?,
    val status: String,
    val dueDate: String?,
    val originalDueDate: String?,
    val paymentDate: String?,
    val clientPaymentDate: String?,
    val installmentNumber: Int?,
    val invoiceUrl: String?,
    val invoiceNumber: String?,
    val externalReference: String?,
    val deleted: Boolean,
    val anticipated: Boolean,
    val anticipable: Boolean,
    val creditDate: String?,
    val estimatedCreditDate: String?,
    val transactionReceiptUrl: String?,
    val nossoNumero: String?,
    val bankSlipUrl: String?,
    val lastInvoiceViewedDate: String?,
    val lastBankSlipViewedDate: String?,
    val discount: Discount,
    val fine: Fine,
    val interest: Interest,
    val postalService: Boolean,
    val custody: String?,
    val refunds: String?
)

data class Discount(
    val value: Double,
    val limitDate: String?,
    val dueDateLimitDays: Int,
    val type: String
)

data class Fine(
    val value: Double,
    val type: String
)

data class Interest(
    val value: Double,
    val type: String
)