package org.pecasonline.features.brand.controller

import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandController
import org.pecasonline.features.brand.IBrandService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(BrandController::class)
class BrandControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var brandService: IBrandService

    @Test
    fun `should return list of brands`() {
        val brands = listOf(
            Brand(id = 1, brandName = "Toyota"),
            Brand(id = 2, brandName = "Honda")
        )
        
        whenever(brandService.getAvailableBrands()).thenReturn(brands)

        mockMvc.get("/api/v1/marcas")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$[0].marca") { value("Toyota") }
                jsonPath("$[1].marca") { value("Honda") }
            }

        verify(brandService).getAvailableBrands()
    }
}
