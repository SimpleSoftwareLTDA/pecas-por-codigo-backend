package org.pecasonline.features.description.controller

import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionsController
import org.pecasonline.features.description.IDescriptionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(DescriptionsController::class)
class DescriptionsControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var descriptionService: IDescriptionService

    @Test
    fun `should return list of descriptions`() {
        val descriptions = listOf(
            Description(id = 1, description = "Description 1"),
            Description(id = 2, description = "Description 2")
        )

        whenever(descriptionService.getAvailableDescriptions()).thenReturn(descriptions)

        mockMvc.get("/api/v1/descricoes")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].descricao") { value("Description 1") }
                jsonPath("$[1].descricao") { value("Description 2") }
            }

        verify(descriptionService).getAvailableDescriptions()
    }

    @Test
    fun `should return empty list when no descriptions are available`() {
        whenever(descriptionService.getAvailableDescriptions()).thenReturn(emptyList())

        mockMvc.get("/api/v1/descricoes")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                content { json("[]") }
            }

        verify(descriptionService).getAvailableDescriptions()
    }
}
