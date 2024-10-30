package org.pecasonline.features.subscription

import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.supplier.domain.Supplier
import org.springframework.stereotype.Service

@Service
class SubscriptionService(
    private val planService: IPlanService,
    private val subscriptionRepository: SubscriptionRepository
) : ISubscriptionService {
    override fun createSubscription(subscriptionDto: CreateSubscriptionDTO, supplier: Supplier): Subscription {
        val chosenPlan = runCatching {
            planService.getPlanById(subscriptionDto.planId)
        }.getOrElse {
            throw IllegalArgumentException("O plano escolhido não existe. planId: ${subscriptionDto.planId}")
        }

        val subscription = subscriptionDto.toSubscription(supplier, chosenPlan)
        return subscriptionRepository.save(subscription)
    }
}