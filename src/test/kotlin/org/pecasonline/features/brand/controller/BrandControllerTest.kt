package org.pecasonline.features.brand.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandController
import org.pecasonline.features.brand.IBrandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(BrandController::class)
@org.springframework.test.context.ActiveProfiles("test")
class BrandControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var brandService: IBrandService

    @Test
    fun `should return list of brands`() {
        val brands = listOf(
            Brand(id = 1, brandName = "Toyota"),
            Brand(id = 2, brandName = "Honda")
        )
        
        every { brandService.getAvailableBrands() } returns brands

        mockMvc.get("/api/v1/marcas")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].marca") { value("Toyota") }
                jsonPath("$[1].marca") { value("Honda") }
            }

        verify { brandService.getAvailableBrands() }
    }
}
