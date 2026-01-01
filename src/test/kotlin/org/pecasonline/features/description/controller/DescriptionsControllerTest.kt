package org.pecasonline.features.description.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionsController
import org.pecasonline.features.description.IDescriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(DescriptionsController::class)
@org.springframework.test.context.ActiveProfiles("test")
class DescriptionsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var descriptionService: IDescriptionService

    @Test
    fun `should return list of descriptions`() {
        val descriptions = listOf(
            Description(id = 1, description = "Description 1"),
            Description(id = 2, description = "Description 2")
        )

        every { descriptionService.getAvailableDescriptions() } returns descriptions

        mockMvc.get("/api/v1/descricoes")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].descricao") { value("Description 1") }
                jsonPath("$[1].descricao") { value("Description 2") }
            }

        verify { descriptionService.getAvailableDescriptions() }
    }

    @Test
    fun `should return empty list when no descriptions are available`() {
        every { descriptionService.getAvailableDescriptions() } returns emptyList()

        mockMvc.get("/api/v1/descricoes")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("[]") }
            }

        verify { descriptionService.getAvailableDescriptions() }
    }
}
