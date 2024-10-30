package org.pecasonline.features.subscription

import org.pecasonline.features.supplier.domain.Supplier

interface ISubscriptionService {
    fun createSubscription(subscriptionDto: CreateSubscriptionDTO, supplier: Supplier): Subscription
}