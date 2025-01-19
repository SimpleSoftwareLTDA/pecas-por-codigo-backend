package org.pecasonline.features.subscription.service

import org.pecasonline.features.subscription.dto.CreateSubscription
import org.pecasonline.features.subscription.entities.Subscription
import org.pecasonline.features.supplier.domain.Supplier

interface ISubscriptionService {
    fun createSubscription(subscriptionDto: CreateSubscription, supplier: Supplier): Subscription
}