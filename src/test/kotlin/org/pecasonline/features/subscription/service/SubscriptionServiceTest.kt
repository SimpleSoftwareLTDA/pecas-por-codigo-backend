package org.pecasonline.features.subscription.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.subscription.CreateSubscriptionDTO
import org.pecasonline.features.subscription.Subscription
import org.pecasonline.features.subscription.SubscriptionRepository
import org.pecasonline.features.subscription.SubscriptionService
import org.pecasonline.features.supplier.domain.Supplier

class SubscriptionServiceTest {

    @Mock
    private lateinit var planService: IPlanService

    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @InjectMocks
    private lateinit var subscriptionService: SubscriptionService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should create subscription successfully`() {
        val supplier = Supplier(
            id = 1,
            name = "Test Supplier",
            supplierOriginalLink = "http://supplierlink.com",
            socialName = "Test Social Name",
            cnpj = "12345678901234",
            stateSubscription = "123456789",
            address = mock(),
            contact = mock()
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

        val subscriptionDto = CreateSubscriptionDTO(
            planId = 1,
            paymentDay = 15,
            bigBannerUrl = "http://bigbanner.com",
            smallBannerUrl = "http://smallbanner.com"
        )

        whenever(planService.getPlanById(subscriptionDto.planId)).thenReturn(plan)
        whenever(subscriptionRepository.save(any())).thenReturn(
            Subscription(
                id = 1,
                paymentDay = subscriptionDto.paymentDay,
                supplier = supplier,
                plan = plan,
                bigBannerUrl = subscriptionDto.bigBannerUrl,
                smallBannerUrl = subscriptionDto.smallBannerUrl
            )
        )

        val subscription = subscriptionService.createSubscription(subscriptionDto, supplier)

        assertEquals(subscription.paymentDay, subscriptionDto.paymentDay)
        assertEquals(subscription.plan, plan)
        assertEquals(subscription.supplier, supplier)
        verify(subscriptionRepository).save(any())
    }

    @Test
    fun `should throw exception when plan does not exist`() {
        val supplier = Supplier(
            id = 1,
            name = "Test Supplier",
            supplierOriginalLink = "http://supplierlink.com",
            socialName = "Test Social Name",
            cnpj = "12345678901234",
            stateSubscription = "123456789",
            address = mock(),
            contact = mock()
        )

        val subscriptionDto = CreateSubscriptionDTO(
            planId = 99,
            paymentDay = 10,
            bigBannerUrl = "http://bigbanner.com",
            smallBannerUrl = "http://smallbanner.com"
        )

        whenever(planService.getPlanById(subscriptionDto.planId)).thenThrow(IllegalArgumentException("O plano escolhido não existe. planId: ${subscriptionDto.planId}"))

        val exception = assertThrows<IllegalArgumentException> {
            subscriptionService.createSubscription(subscriptionDto, supplier)
        }

        assertEquals("O plano escolhido não existe. planId: ${subscriptionDto.planId}", exception.message)
    }
}
