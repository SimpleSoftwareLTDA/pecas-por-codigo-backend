package org.pecasonline.features.subscription.repository

import org.pecasonline.features.subscription.entities.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository: JpaRepository<Subscription, Long> {

    @Query("SELECT s.bigBannerUrl FROM signature s")
    fun findBigBannerUrls(): List<String?>
}