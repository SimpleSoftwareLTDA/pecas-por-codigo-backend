package org.pecasonline.features.subscription

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.features.banking.BankingService
import org.pecasonline.common.httpclients.dto.CreateSubscriptionRequest
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val logger = KotlinLogging.logger {}

@Service
class SubscriptionService(
    private val planService: IPlanService,
    private val bankingService: BankingService,
    private val subscriptionRepository: SubscriptionRepository,
    private val supplierRepository: SupplierRepository
) : ISubscriptionService {
    override fun createSubscription(subscriptionDto: CreateSubscriptionDTO, supplier: Supplier): Subscription {
        val chosenPlan = runCatching {
            planService.getPlanById(subscriptionDto.planId)
        }.getOrElse {
            throw IllegalArgumentException("O plano escolhido não existe. planId: ${subscriptionDto.planId}")
        }

        val subscriptionRequest = CreateSubscriptionRequest(
            customer = supplier.asaasId!!,
            billingType = "BOLETO",
            nextDueDate = calculateNextDueDate(subscriptionDto.paymentDay),
            value = SubscriptionPlan.priceFromId(subscriptionDto.planId),
            cycle = getCycle(1),
            description = "Assinatura do fornecedor ${supplier.name} no plano ${SubscriptionPlan.nameFromId(subscriptionDto.planId)}"
        )

        bankingService.createSubscription(subscriptionRequest)

        val subscription = subscriptionDto.toSubscription(supplier, chosenPlan)

        return subscriptionRepository.save(subscription)
    }

    private fun getCycle(option: Int): String =
        when (option) {
            1 -> "MONTHLY"
            2 -> "YEARLY"

            else -> throw InvalidSubscriptionException()
        }

    @Transactional
    fun updateSubscriptionStatusByWebhook(asaasCustomerId: String, asaasStatus: String) {
        val supplier = supplierRepository.findByAsaasId(asaasCustomerId)
            ?: throw IllegalArgumentException("Fornecedor com Asaas ID $asaasCustomerId não encontrado")

        val subscription = supplier.subscription
            ?: throw IllegalStateException("Fornecedor com Asaas ID $asaasCustomerId não possui uma assinatura associada")

        val newStatus = asaasStatus.toSubscriptionStatus()

        subscription.status = newStatus

        subscriptionRepository.save(subscription)

        logger.info { "Status da assinatura de ${supplier.name} foi alterado para: $newStatus" }
    }

    fun calculateNextDueDate(paymentDay: Int): String {
        val currentDate = LocalDate.now()
        val nextDueDate = currentDate.withDayOfMonth(paymentDay)
            .let {
                if (it.isBefore(currentDate)) {
                    // Se a data já passou neste mês, ajusta para o próximo mês
                    it.plusMonths(1)
                } else {
                    it
                }
            }
        return nextDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE) // Formato yyyy-MM-dd
    }

    fun checkIfSubscriptionIsActive(
        supplier: List<Supplier>,
        cnpj: String
    ) {
        when {
            supplier.first().subscription?.status != SubscriptionStatus.ACTIVE -> {
                val errorMessage = "Fornecedor com CNPJ: $cnpj não tem uma assinatura ativa."

                logger.error { errorMessage }

                error(errorMessage)
            }
        }
    }
}

fun String.toSubscriptionStatus(): SubscriptionStatus = when (this) {
    "RECEIVED_IN_CASH", "PAYMENT_RECEIVED" -> SubscriptionStatus.ACTIVE
    "PENDING", "AWAITING_PAYMENT" -> SubscriptionStatus.INACTIVE
    "OVERDUE", "PAYMENT_OVERDUE" -> SubscriptionStatus.LATE

    else -> throw IllegalArgumentException("Status do webhook desconhecido: $this")
}
