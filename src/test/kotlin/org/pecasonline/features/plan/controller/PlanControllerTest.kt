package org.pecasonline.features.plan.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.pecasonline.features.plan.IPlanService
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.plan.PlanController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(PlanController::class)
@org.springframework.test.context.ActiveProfiles("test")
class PlanControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var planService: IPlanService

    @Test
    fun `should return all available plans`() {
        val plans = listOf(
            Plan(name = "Basic Plan", priceInCents = 5000, stock = true, quote = false, smallBanner = true, bigBanner = false),
            Plan(name = "Premium Plan", priceInCents = 15000, stock = false, quote = true, smallBanner = false, bigBanner = true)
        )
        
        every { planService.getAvailablePlans() } returns plans

        mockMvc.get("/api/v1/planos")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].nome") { value("Basic Plan") }
                jsonPath("$[1].nome") { value("Premium Plan") }
            }

        verify { planService.getAvailablePlans() }
    }
}
