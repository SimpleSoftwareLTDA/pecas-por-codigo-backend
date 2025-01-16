package org.pecasonline.common.httpclients

import com.fasterxml.jackson.annotation.JsonProperty
import feign.RequestInterceptor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "asaasClient", url = "\${asaas.api.base-url}", configuration = [FeignConfig::class])
interface AsaasClient {

    @PostMapping("/customers")
    fun createCustomer(
        @RequestBody request: CreateClientRequest
    ): CreateClientResponse

    @PostMapping("/subscriptions")
    fun createSubscription(
        @RequestBody request: CreateSubscriptionRequest
    ): CreateSubscriptionResponse

    @GetMapping("/customers/{id}")
    fun getCustomerById(
        @PathVariable("id") id: String
    ): CreateClientResponse

}

@Configuration
@ConfigurationProperties(prefix = "asaas.api")
data class AsaasProperties(

    var apiKey: String = "",
)


@Configuration
class FeignConfig(
    private val asaasProperties: AsaasProperties
) {

    @Bean
    fun requestInterceptor(): RequestInterceptor =
        RequestInterceptor { template ->
            template.header("accept", "application/json")
            template.header("content-type", "application/json")
            template.header("access_token", asaasProperties.apiKey)
        }
}

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

data class Discount(
    val value: Double,
    val dueDateLimitDays: Int
)

data class Fine(
    val value: Double
)

data class Interest(
    val value: Double
)

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

data class DiscountResponse(
    val value: Double,
    val dueDateLimitDays: Int
)

data class FineResponse(
    val value: Double
)

data class InterestResponse(
    val value: Double
)
