package org.pecasonline.features.subscription.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.httpclients.dto.CreateClientRequest
import org.pecasonline.common.httpclients.dto.CreateClientResponse
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.subscription.dto.AsaasWebhook
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
class SubscriptionController(
    private val bankingService: BankingService,
    private val subscriptionService: SubscriptionService
) {

    @PostMapping("/customers")
    fun createCustomer(@RequestBody request: CreateClientRequest): ResponseEntity<CreateClientResponse> {
        val response = bankingService.createCustomer(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/v1/api/payments/status")
    fun handleWebhook(@RequestBody payload: AsaasWebhook): ResponseEntity<String> {
        logger.debug { "Recebido Webhook do Asaas: $payload" }

        val asaasCustomerId = payload.payment.customer


        subscriptionService.updateSubscriptionStatusByWebhook(asaasCustomerId, payload.event)

        return ResponseEntity.status(HttpStatus.OK).body("Webhook recebido com sucesso!")
    }
}