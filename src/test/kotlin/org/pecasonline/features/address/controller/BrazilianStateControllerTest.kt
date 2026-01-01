package org.pecasonline.features.address.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.service.IStateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(BrazilianStateController::class)
@org.springframework.test.context.ActiveProfiles("test")
class BrazilianStateControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var stateService: IStateService

    @Test
    fun `should return list of Brazilian states`() {
        val states = listOf(
            BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP"),
            BrazilianState(id = 2, stateName = "Rio de Janeiro", stateCode = "RJ")
        )

        every { stateService.getAvailableStates() } returns states

        mockMvc.get("/api/v1/estados")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].nome") { value("São Paulo") }
                jsonPath("$[0].sigla") { value("SP") }
                jsonPath("$[1].nome") { value("Rio de Janeiro") }
                jsonPath("$[1].sigla") { value("RJ") }
            }

        verify { stateService.getAvailableStates() }
    }
}
