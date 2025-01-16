package org.pecasonline.common.httpclients

import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class AsaasService(
    private val asaasClient: AsaasClient
) {
    private val logger = KotlinLogging.logger {}

    fun createCustomer(request: CreateClientRequest): CreateClientResponse {
        return try {
            logger.info { "Calling Asaas API to create a customer: ${request.name}" }
            val response = asaasClient.createCustomer(request)
            logger.info { "Customer created successfully. ID: ${response.id}" }
            response
        } catch (ex: RuntimeException) {
            logger.error(ex) { "Failed to create customer in Asaas API" }
            throw ex
        }
    }

    fun createSubscription(request: CreateSubscriptionRequest): CreateSubscriptionResponse = asaasClient.createSubscription(request)

    fun checkIfCustomerExists(asaasId: String): Boolean =
        try {
            asaasClient.getCustomerById(asaasId)
            true // Se o cliente foi encontrado, a chamada retorna 200 e o cliente existe
        } catch (ex: FeignException) {
            when {
                ex.status() == 404 -> false // Se o status for 404, o cliente não existe

                else -> throw RuntimeException("Erro ao verificar a existência do cliente com ID $asaasId", ex)
            }
        }


}




