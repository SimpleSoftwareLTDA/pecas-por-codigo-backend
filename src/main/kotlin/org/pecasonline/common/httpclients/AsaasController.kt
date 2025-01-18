package org.pecasonline.common.httpclients

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping
class AsaasController(
    private val asaasService: AsaasService
) {

    @PostMapping("/customers")
    fun createCustomer(@RequestBody request: CreateClientRequest): ResponseEntity<CreateClientResponse> {
        val response = asaasService.createCustomer(request)
        return ResponseEntity.ok(response)
    }


    @RestController
    @RequestMapping("/v1/api/payments/status")
    class AsaasWebhookController {

        @PostMapping
        fun handleWebhook(@RequestBody payload: Map<String, Any>): ResponseEntity<String> {
            logger.info { "Recebido Webhook do Asaas: $payload" }

            // Aqui você pode processar o payload recebido
            // Exemplo: verificar o evento e atualizar o status no banco de dados

            return ResponseEntity.status(HttpStatus.OK).body("Webhook recebido com sucesso!")
        }
    }

}