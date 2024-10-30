package org.pecasonline.features.subscription

import org.springframework.data.jpa.repository.JpaRepository

interface SubscriptionRepository: JpaRepository<Subscription, Int>