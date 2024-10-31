package org.pecasonline.features.address.controller

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.service.IStateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.http.MediaType

@WebMvcTest(BrazilianStateController::class)
class BrazilianStateControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var stateService: IStateService

    @Test
    fun `should return list of Brazilian states`() {
        val states = listOf(
            BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP"),
            BrazilianState(id = 2, stateName = "Rio de Janeiro", stateCode = "RJ")
        )

        whenever(stateService.getAvailableStates()).thenReturn(states)

        mockMvc.get("/api/v1/estados")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].nome") { value("São Paulo") }
                jsonPath("$[0].sigla") { value("SP") }
                jsonPath("$[1].nome") { value("Rio de Janeiro") }
                jsonPath("$[1].sigla") { value("RJ") }
            }

        verify(stateService).getAvailableStates()
    }
}
