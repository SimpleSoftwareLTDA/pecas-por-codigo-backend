package org.pecasonline.features.subscription.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(SubscriptionController::class)
@org.springframework.test.context.ActiveProfiles("test")
class SubscriptionControllerCnpjValidationTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var subscriptionService: SubscriptionService

    @MockkBean
    private lateinit var bankingService: BankingService

    private val unformattedCnpj = "04659416000177"
    private val formattedCnpj = "04.659.416/0001-77"
    private val invalidCnpj = "11.222.333/0001-99"
    private val bannerUrl = "https://example.com/banner.jpg"

    @Test
    fun `should accept unformatted valid CNPJ`() {
        every { subscriptionService.setBigBannerUrlForSupplier(any(), any()) } returns Unit

        mockMvc.post("/api/v1/banner") {
            param("novo-banner", bannerUrl)
            param("cnpj", unformattedCnpj)
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun `should accept formatted valid CNPJ`() {
        every { subscriptionService.setBigBannerUrlForSupplier(any(), any()) } returns Unit

        mockMvc.post("/api/v1/banner") {
            param("novo-banner", bannerUrl)
            param("cnpj", formattedCnpj)
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun `should reject invalid CNPJ`() {
        mockMvc.post("/api/v1/banner") {
            param("novo-banner", bannerUrl)
            param("cnpj", invalidCnpj)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
