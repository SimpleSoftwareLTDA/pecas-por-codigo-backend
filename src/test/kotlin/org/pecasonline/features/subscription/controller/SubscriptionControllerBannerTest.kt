package org.pecasonline.features.subscription.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest(SubscriptionController::class)
@org.springframework.test.context.ActiveProfiles("test")
class SubscriptionControllerBannerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var subscriptionService: SubscriptionService

    @MockkBean
    private lateinit var bankingService: BankingService

    @Test
    fun `should return a random banner URL`() {
        // Given
        val bannerUrls = listOf("https://example.com/banner1.jpg", "https://example.com/banner2.jpg")
        every { subscriptionService.getBigBannerUrls() } returns bannerUrls

        // When & Then
        mockMvc.get("/api/v1/banner")
            .andExpect {
                status { isOk() }
                // Relaxed content type check
                content { contentTypeCompatibleWith(MediaType.TEXT_PLAIN) }
                // We can't assert the exact URL since it's randomly selected
                // Just verify that the response is not empty
                content { string(not(emptyString())) }
            }

        verify { subscriptionService.getBigBannerUrls() }
    }

    @Test
    fun `should set a banner URL for a supplier`() {
        // Given
        val cnpj = "12345678000195" // Valid CNPJ for testing
        val formattedCnpj = "12.345.678/0001-95"
        val newBannerUrl = "https://example.com/new-banner.jpg"

        // O controller chama formatCnpj internamente, então o mock deve refletir a normalização
        every { subscriptionService.setBigBannerUrlForSupplier(cnpj = formattedCnpj, newBannerUrl = newBannerUrl) } returns Unit

        // When & Then
        mockMvc.post("/api/v1/banner") {
            param("novo-banner", newBannerUrl)
            param("cnpj", cnpj)
        }.andExpect {
            status { isAccepted() }
        }

        verify { subscriptionService.setBigBannerUrlForSupplier(cnpj = formattedCnpj, newBannerUrl = newBannerUrl) }
    }

    @Test
    fun `should return 400 when setting a banner URL for a supplier with invalid CNPJ`() {
        // Given
        val invalidCnpj = "12345678000100" // Invalid CNPJ (wrong check digits)
        val newBannerUrl = "https://example.com/new-banner.jpg"

        // When & Then
        mockMvc.post("/api/v1/banner") {
            param("novo-banner", newBannerUrl)
            param("cnpj", invalidCnpj)
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { subscriptionService.setBigBannerUrlForSupplier(any(), any()) }
    }

    @Test
    fun `should return all banner URLs`() {
        // Given
        val bannerUrls = listOf("https://example.com/banner1.jpg", "https://example.com/banner2.jpg")
        every { subscriptionService.getBigBannerUrls() } returns bannerUrls

        // When & Then
        mockMvc.get("/api/v1/banner/all")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0]") { value(bannerUrls[0]) }
                jsonPath("$[1]") { value(bannerUrls[1]) }
            }

        verify { subscriptionService.getBigBannerUrls() }
    }
}
