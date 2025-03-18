package org.pecasonline.features.subscription.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.httpclients.dto.CreateClientRequest
import org.pecasonline.common.httpclients.dto.CreateClientResponse
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.subscription.dto.AsaasWebhook
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ThreadLocalRandom

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

    @PostMapping("/api/v1/payments/status")
    fun handleWebhook(@RequestBody payload: AsaasWebhook): ResponseEntity<String> {
        logger.debug { "Recebido Webhook do Asaas: $payload" }

        val asaasCustomerId = payload.payment.customer


        subscriptionService.updateSubscriptionStatusByWebhook(asaasCustomerId, payload.event)

        return ResponseEntity.status(HttpStatus.OK).body("Webhook recebido com sucesso!")
    }

    private val bannerUrls = mutableListOf<String>()

    @GetMapping("/api/v1/banner")
    @Cacheable("banners")
    fun getBannerUrl(): String? {
        when {
            bannerUrls.isEmpty() -> bannerUrls.addAll(subscriptionService.getBigBannerUrls().map { url ->
                val defaultBannerUrl = "https://pub-6506d3d953f94560b493b69d4b68f549.r2.dev/Pe%C3%A7as%20Online%20X%20-%20Template%20Para%20Banner.png"

                url.ifBlank { defaultBannerUrl }
            })
        }

        when {
            bannerUrls.isEmpty() -> return null

            else -> {
                val randomIndex = ThreadLocalRandom.current().nextInt(bannerUrls.size)

                return bannerUrls[randomIndex]
            }
        }
    }
}