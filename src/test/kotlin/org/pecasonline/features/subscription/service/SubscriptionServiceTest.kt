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

    @Mock
    private lateinit var planService: IPlanService

    @Mock
    private lateinit var subscriptionRepository: SubscriptionRepository

    @Mock
    private lateinit var bankingService: BankingService

    @Mock
    private lateinit var supplierRepository: SupplierRepository

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
            cnpj = "15826705000130",
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

        val subscriptionDto = CreateSubscription(
            planId = 1,
            paymentDay = 15,
            bigBannerUrl = "http://bigbanner.com",
            smallBannerUrl = "http://smallbanner.com"
        )

        whenever(planService.getPlanById(subscriptionDto.planId)).thenReturn(plan)
        whenever(subscriptionRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<Subscription>(0)
        }

        val subscription = subscriptionService.createSubscription(subscriptionDto, supplier)

        assertEquals(subscription.paymentDay, subscriptionDto.paymentDay)
        assertEquals(subscription.plan, plan)
        assertEquals(subscription.supplier, supplier)
        assertEquals(org.pecasonline.features.subscription.entities.SubscriptionStatus.ACTIVE, subscription.status)
        verify(subscriptionRepository).save(any())
    }

    @Test
    fun `calculateNextDueDate should return date 30 days from now`() {
        // Arrange
        val paymentDay = 15 // This should be ignored in our implementation
        val expectedDate = LocalDate.now().plusDays(30).format(DateTimeFormatter.ISO_LOCAL_DATE)

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
            address = mock(),
            contact = mock()
        )

        val subscriptionDto = CreateSubscription(
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
