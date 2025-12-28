package org.pecasonline.features.subscription.controller

import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.emptyString
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.common.Constants
import org.pecasonline.features.subscription.service.SubscriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.pecasonline.features.banking.BankingService

@WebMvcTest(SubscriptionController::class)
@org.springframework.test.context.ActiveProfiles("test")
class SubscriptionControllerBannerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry


    @MockBean
    private lateinit var subscriptionService: SubscriptionService

    @MockBean
    private lateinit var bankingService: BankingService

    @Test
    fun `should return a random banner URL`() {
        // Given
        val bannerUrls = listOf("https://example.com/banner1.jpg", "https://example.com/banner2.jpg")
        whenever(subscriptionService.getBigBannerUrls()).thenReturn(bannerUrls)

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

        verify(subscriptionService).getBigBannerUrls()
    }

    @Test
    fun `should set a banner URL for a supplier`() {
        // Given
        val cnpj = "12345678000195" // Valid CNPJ for testing
        val newBannerUrl = "https://example.com/new-banner.jpg"

        // When & Then
        mockMvc.post("/api/v1/banner") {
            param("novo-banner", newBannerUrl)
            param("cnpj", cnpj)
        }.andExpect {
            status { isAccepted() }
        }

        verify(subscriptionService).setBigBannerUrlForSupplier(cnpj = cnpj, newBannerUrl = newBannerUrl)
    }

    @Test
    fun `should return all banner URLs`() {
        // Given
        val bannerUrls = listOf("https://example.com/banner1.jpg", "https://example.com/banner2.jpg")
        whenever(subscriptionService.getBigBannerUrls()).thenReturn(bannerUrls)

        // When & Then
        mockMvc.get("/api/v1/banner/all")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0]") { value(bannerUrls[0]) }
                jsonPath("$[1]") { value(bannerUrls[1]) }
            }

        verify(subscriptionService).getBigBannerUrls()
    }
}
