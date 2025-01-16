package org.pecasonline.features.subscription

import org.pecasonline.common.httpclients.AsaasService
import org.pecasonline.common.httpclients.CreateSubscriptionRequest
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.supplier.domain.Supplier
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SubscriptionService(
    private val planService: IPlanService,
    private val asaasService: AsaasService,
    private val subscriptionRepository: SubscriptionRepository
) : ISubscriptionService {
    override fun createSubscription(subscriptionDto: CreateSubscriptionDTO, supplier: Supplier): Subscription {
        val chosenPlan = runCatching {
            planService.getPlanById(subscriptionDto.planId)
        }.getOrElse { throw IllegalArgumentException("O plano escolhido não existe. planId: ${subscriptionDto.planId}") }

        // TODO: Criar a assinatura no asaas com base nos dados que existem na entidade supplier e contact

        val subscriptionRequest = CreateSubscriptionRequest(
            customer = supplier.asaasId!!,
            billingType = "BOLETO",
            nextDueDate = calculateNextDueDate(subscriptionDto.paymentDay),
            value = SubscriptionPlan.priceFromId(subscriptionDto.planId),
            cycle = "MONTHLY",
            description = "Assinatura do fornecedor ${supplier.name} no plano ${SubscriptionPlan.nameFromId(subscriptionDto.planId)}"
        )

        asaasService.createSubscription(subscriptionRequest)

        val subscription = subscriptionDto.toSubscription(supplier, chosenPlan)

        return subscriptionRepository.save(subscription)
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
}