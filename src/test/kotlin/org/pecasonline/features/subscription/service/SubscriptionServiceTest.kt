package org.pecasonline.features.subscription.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.httpclients.dto.CreateSubscriptionResponse
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.subscription.dto.CreateSubscription
import org.pecasonline.features.subscription.entities.Subscription
import org.pecasonline.features.subscription.entities.SubscriptionStatus
import org.pecasonline.features.subscription.repository.SubscriptionRepository
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SubscriptionServiceTest {

    private val planService = mockk<IPlanService>()
    private val subscriptionRepository = mockk<SubscriptionRepository>()
    private val bankingService = mockk<BankingService>()
    private val supplierRepository = mockk<SupplierRepository>()

    private val subscriptionService = SubscriptionService(
        planService,
        bankingService,
        subscriptionRepository,
        supplierRepository
    )

    @Test
    fun `should create subscription successfully`() {
        val supplier = Supplier(
            id = 1,
            name = "Test Supplier",
            supplierOriginalLink = "http://supplierlink.com",
            socialName = "Test Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = mockk(),
            contact = mockk(),
            asaasId = "some-id"
        )

        val plan = Plan(
            id = 1,
            name = "Premium Plan",
            priceInCents = 9999,
            stock = true,
            quote = true,
            smallBanner = true,
            bigBanner = true
        )

        val subscriptionDto = CreateSubscription(
            planId = 1,
            paymentDay = 15,
            bigBannerUrl = "http://bigbanner.com",
            smallBannerUrl = "http://smallbanner.com"
        )

        every { planService.getPlanById(subscriptionDto.planId) } returns plan
        every { bankingService.createSubscription(any()) } returns mockk<CreateSubscriptionResponse>()
        every { subscriptionRepository.save(any()) } answers { it.invocation.args[0] as Subscription }

        val subscription = subscriptionService.createSubscription(subscriptionDto, supplier)

        assertEquals(subscription.paymentDay, subscriptionDto.paymentDay)
        assertEquals(subscription.plan, plan)
        assertEquals(subscription.supplier, supplier)
        assertEquals(SubscriptionStatus.ACTIVE, subscription.status)
        verify { subscriptionRepository.save(any()) }
    }

    @Test
    fun `calculateNextDueDate should return date 30 days from now`() {
        // Arrange
        val paymentDay = 15
        val currentDate = LocalDate.now()
        val expectedNextDate = currentDate.withDayOfMonth(paymentDay).let {
            if (it.isBefore(currentDate)) it.plusMonths(1) else it
        }
        val expectedDate = expectedNextDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Act
        val result = subscriptionService.calculateNextDueDate(paymentDay)

        // Assert
        assertEquals(expectedDate, result)
    }

    @Test
    fun `should throw exception when plan does not exist`() {
        val supplier = Supplier(
            id = 1,
            name = "Test Supplier",
            supplierOriginalLink = "http://supplierlink.com",
            socialName = "Test Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = mockk(),
            contact = mockk(),
            asaasId = "some-id"
        )

        val subscriptionDto = CreateSubscription(
            planId = 99,
            paymentDay = 10,
            bigBannerUrl = "http://bigbanner.com",
            smallBannerUrl = "http://smallbanner.com"
        )

        every { planService.getPlanById(subscriptionDto.planId) } throws IllegalArgumentException("O plano escolhido não existe. planId: ${subscriptionDto.planId}")

        val exception = assertThrows<IllegalArgumentException> {
            subscriptionService.createSubscription(subscriptionDto, supplier)
        }

        assertEquals("O plano escolhido não existe. planId: ${subscriptionDto.planId}", exception.message)
    }
}
