package org.pecasonline.features.subscription.repository

import org.pecasonline.features.subscription.entities.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface SubscriptionRepository: JpaRepository<Subscription, Long> {

    @Query("SELECT s.bigBannerUrl FROM signature s")
    fun findBigBannerUrls(): List<String?>

    @Query("""
        SELECT s.cnpj, sig.big_banner_url 
        FROM signature sig 
        JOIN supplier s ON sig.supplier_id = s.id 
        WHERE sig.big_banner_url IS NOT NULL AND sig.big_banner_url != ''
    """, nativeQuery = true)
    fun findBigBannerDetails(): List<Array<Any>>

    @Transactional
    @Modifying
    @Query("""
        UPDATE signature 
        SET big_banner_url = :bigBannerUrl
        WHERE supplier_id = (SELECT s.id FROM supplier s WHERE s.cnpj = :cnpj)
    """, nativeQuery = true)
    fun updateBigBannerUrlByCnpj(@Param("cnpj") cnpj: String, @Param("bigBannerUrl") bigBannerUrl: String): Int

}