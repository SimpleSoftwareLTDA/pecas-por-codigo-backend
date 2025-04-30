package org.pecasonline.common.httpclients

import org.pecasonline.common.httpclients.config.AsaasHeadersConfig
import org.pecasonline.common.httpclients.dto.CreateClientRequest
import org.pecasonline.common.httpclients.dto.CreateClientResponse
import org.pecasonline.common.httpclients.dto.CreateSubscriptionRequest
import org.pecasonline.common.httpclients.dto.CreateSubscriptionResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "asaasClient", url = "\${asaas.api.base-url}", configuration = [AsaasHeadersConfig::class])
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
