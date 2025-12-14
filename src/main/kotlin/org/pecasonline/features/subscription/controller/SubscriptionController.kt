package org.pecasonline.features.subscription.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.hibernate.validator.constraints.br.CNPJ
import org.pecasonline.common.Constants.DEFAULT_BANNER_URL
import org.pecasonline.common.httpclients.dto.CreateClientRequest
import org.pecasonline.common.httpclients.dto.CreateClientResponse
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.subscription.dto.AsaasWebhook
import org.pecasonline.features.subscription.service.SubscriptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ThreadLocalRandom

private val logger = KotlinLogging.logger {}

@RestController
class SubscriptionController(
    private val bankingService: BankingService,
    private val subscriptionService: SubscriptionService,
    private val meterRegistry: io.micrometer.core.instrument.MeterRegistry
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
        meterRegistry.counter("subscription.webhook", "event", payload.event.toString()).increment()

        return ResponseEntity.status(HttpStatus.OK).body("Webhook recebido com sucesso!")
    }

    private val bannerUrls = mutableListOf<String>()

    @GetMapping("/api/v1/banner")
    fun getBannerUrl(): String {
        val bannerUrls = subscriptionService.getBigBannerUrls().filter { it.isNotBlank() }
        val availableBanners = bannerUrls + DEFAULT_BANNER_URL

        val randomIndex = ThreadLocalRandom.current().nextInt(availableBanners.size)
        return availableBanners[randomIndex]
    }

    @PostMapping("/api/v1/banner")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun setBannerUrl(@RequestParam("novo-banner") newBannerUrl: String, @RequestParam("cnpj") @CNPJ cnpj: String) {
        subscriptionService.setBigBannerUrlForSupplier(newBannerUrl = newBannerUrl, cnpj = cnpj)
        meterRegistry.counter("banner.update", "cnpj", cnpj).increment()
    }

    @GetMapping("/api/v1/banner/all")
    fun getAllBannerUrls(): List<String> {
        return subscriptionService.getBigBannerUrls().filter { it.isNotBlank() }
    }
}
