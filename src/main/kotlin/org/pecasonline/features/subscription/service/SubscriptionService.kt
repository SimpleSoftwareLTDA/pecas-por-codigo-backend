package org.pecasonline.features.subscription.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.features.banking.BankingService
import org.pecasonline.common.httpclients.dto.CreateSubscriptionRequest
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.subscription.constants.SubscriptionPlan
import org.pecasonline.features.subscription.dto.CreateSubscription
import org.pecasonline.features.subscription.entities.InvalidSubscriptionException
import org.pecasonline.features.subscription.entities.Subscription
import org.pecasonline.features.subscription.entities.SubscriptionStatus
import org.pecasonline.features.subscription.repository.SubscriptionRepository
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
    override fun createSubscription(subscriptionDto: CreateSubscription, supplier: Supplier): Subscription {
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
        subscription.status = SubscriptionStatus.ACTIVE

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
                when {
                    it.isBefore(currentDate) -> it.plusMonths(1) // Se a data já passou neste mês, ajusta para o próximo mês

                    else -> it
                }
            }
        return nextDueDate.format(DateTimeFormatter.ISO_LOCAL_DATE) // Formato yyyy-MM-dd
    }

    fun checkIfSubscriptionIsActiveOrThrow(
        supplier: Supplier,
        cnpj: String
    ) {
        when {
            supplier.subscription?.status != SubscriptionStatus.ACTIVE -> {
                logger.info { supplier.subscription?.status }

                val errorMessage = "Fornecedor com CNPJ: $cnpj não tem uma assinatura ativa."

                logger.info { errorMessage }

                error(errorMessage)
            }
        }
    }

    fun checkIfSubscriptionIsVipOrThrow(
        supplier: Supplier,
        cnpj: String
    ) {
        when {
            supplier.subscription?.status != SubscriptionStatus.ACTIVE && supplier.subscription?.plan?.name == SubscriptionPlan.VIP.name -> {
                logger.info { supplier.subscription?.status }

                val errorMessage = "Fornecedor com CNPJ: $cnpj não tem uma assinatura ativa."

                logger.info { errorMessage }

                error(errorMessage)
            }
        }
    }

    fun getBigBannerUrls(): List<String> = subscriptionRepository.findBigBannerUrls().filterNotNull()

    fun setBigBannerUrlForSupplier(cnpj: String, newBannerUrl: String) {
        val supplier = supplierRepository.findSupplierByCnpj(cnpj)
            ?: supplierRepository.findSupplierByCnpj(formatCnpj(cnpj))
            ?: throw IllegalArgumentException("Fornecedor com CNPJ: $cnpj não encontrado.")

        checkIfSubscriptionIsActiveOrThrow(supplier = supplier, cnpj = supplier.cnpj)

        checkIfSubscriptionIsVipOrThrow(supplier = supplier, cnpj = supplier.cnpj)

        subscriptionRepository.updateBigBannerUrlByCnpj(
            cnpj = supplier.cnpj,
            bigBannerUrl = newBannerUrl
        )
    }

    private fun formatCnpj(cnpj: String): String {
        val digits = cnpj.filter { it.isDigit() }
        if (digits.length != 14) return cnpj
        return "${digits.substring(0, 2)}.${digits.substring(2, 5)}.${digits.substring(5, 8)}/${digits.substring(8, 12)}-${digits.substring(12, 14)}"
    }
}

fun String.toSubscriptionStatus(): SubscriptionStatus = when (this) {
    "RECEIVED_IN_CASH", "PAYMENT_RECEIVED" -> SubscriptionStatus.ACTIVE
    "PENDING", "AWAITING_PAYMENT" -> SubscriptionStatus.INACTIVE
    "OVERDUE", "PAYMENT_OVERDUE" -> SubscriptionStatus.LATE

    else -> throw IllegalArgumentException("Status do webhook desconhecido: $this")
}
