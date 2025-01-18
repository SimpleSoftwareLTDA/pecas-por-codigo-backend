package org.pecasonline.features.subscription.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.AddressRepository
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.plan.PlanRepository
import org.pecasonline.features.subscription.Subscription
import org.pecasonline.features.subscription.SubscriptionRepository
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class SubscriptionRepositoryTest @Autowired constructor(
    val subscriptionRepository: SubscriptionRepository,
    val supplierRepository: SupplierRepository,
    val planRepository: PlanRepository,
    val addressRepository: AddressRepository,
    val brazilianStateRepository: BrazilianStatesRepository
){

    lateinit var supplier: Supplier
    lateinit var plan: Plan

    @BeforeEach
    fun setUp() {
        val state = brazilianStateRepository.save(
            BrazilianState(
                stateCode = "SP",
                stateName = "Sao Paulo"
            )
        )

        val address = addressRepository.save(
            Address(
                street = "123 Main St",
                city = "Springfield",
                state = state,
                cep = "12345-678",
                country = "Brazil"
            )
        )

        plan = planRepository.save(
            Plan(
                name = "Standard Plan",
                priceInCents = 5000,
                stock = true,
                quote = false,
                smallBanner = true,
                bigBanner = true
            )
        )

        val contact = Contact(
            sellerName = "John Doe",
            itemsEmail = "items@example.com",
            itemsPhone = "555-1234",
            whatsapp = "555-5678",
            itemsWhatsapp = "555-8765",
            stockEmail = "stock@example.com",
            billingEmail = "billing@example.com",
            nfEmail = "nf@example.com",
            site = "http://example.com"
        )

        supplier = supplierRepository.save(
            Supplier(
                name = "Supplier A",
                socialName = "Supplier Social Name",
                cnpj = "12345678000100",
                stateSubscription = "12345678",
                address = address,
                contact = contact
            )
        )
    }

    @Test
    fun `should save and retrieve subscription successfully`() {
        val subscription = subscriptionRepository.save(
            Subscription(
                paymentDay = 15,
                supplier = supplier,
                plan = plan,
                bigBannerUrl = "http://example.com/big-banner",
                smallBannerUrl = "http://example.com/small-banner"
            )
        )

        val retrievedSubscription = subscriptionRepository.findById(subscription.id!!)
        assertTrue(retrievedSubscription.isPresent)
        assertEquals(subscription.id, retrievedSubscription.get().id)
        assertEquals(subscription.paymentDay, retrievedSubscription.get().paymentDay)
        assertEquals(subscription.supplier.id, retrievedSubscription.get().supplier.id)
        assertEquals(subscription.plan.id, retrievedSubscription.get().plan.id)
    }

    @Test
    fun `should delete subscription by id`() {
        val subscription = subscriptionRepository.save(
            Subscription(
                paymentDay = 15,
                supplier = supplier,
                plan = plan,
                bigBannerUrl = "http://example.com/big-banner",
                smallBannerUrl = "http://example.com/small-banner"
            )
        )

        subscriptionRepository.deleteById(subscription.id!!)
        val deletedSubscription = subscriptionRepository.findById(subscription.id!!)
        assertFalse(deletedSubscription.isPresent)
    }
}
