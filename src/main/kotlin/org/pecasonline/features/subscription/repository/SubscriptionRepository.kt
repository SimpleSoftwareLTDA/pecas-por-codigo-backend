package org.pecasonline.features.subscription.repository

import org.pecasonline.features.subscription.entities.Subscription
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriptionRepository: JpaRepository<Subscription, Long>