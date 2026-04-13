package org.pecasonline.features.stock.controller

import org.pecasonline.features.stock.StockController
import org.pecasonline.features.stock.IStockService

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.multipart

@WebMvcTest(StockController::class)
@org.springframework.test.context.ActiveProfiles("test")
class StockControllerCnpjValidationTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean(relaxed = true)
    private lateinit var stringRedisTemplate: org.springframework.data.redis.core.StringRedisTemplate

    @MockkBean(relaxed = true)
    private lateinit var objectMapper: com.fasterxml.jackson.databind.ObjectMapper

    @MockkBean
    private lateinit var stockService: IStockService

    private val unformattedCnpj = "04659416000177"
    private val formattedCnpj = "04.659.416/0001-77"
    private val invalidCnpj = "11.222.333/0001-99"

    @Test
    fun `should accept unformatted valid CNPJ in stock upload`() {
        val file = MockMultipartFile("file", "test.csv", "text/csv", "test content".toByteArray())
        every { stockService.createStock(any(), any(), any(), any(), any()) } returns Unit

        mockMvc.multipart("/api/v1/estoque/estoque-by-cnpj") {
            file(file)
            param("cnpj", unformattedCnpj)
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun `should accept formatted valid CNPJ in stock upload`() {
        val file = MockMultipartFile("file", "test.csv", "text/csv", "test content".toByteArray())
        every { stockService.createStock(any(), any(), any(), any(), any()) } returns Unit

        mockMvc.multipart("/api/v1/estoque/estoque-by-cnpj") {
            file(file)
            param("cnpj", formattedCnpj)
        }.andExpect {
            status { isAccepted() }
        }
    }

    @Test
    fun `should reject invalid CNPJ in stock upload`() {
        val file = MockMultipartFile("file", "test.csv", "text/csv", "test content".toByteArray())

        mockMvc.multipart("/api/v1/estoque/estoque-by-cnpj") {
            file(file)
            param("cnpj", invalidCnpj)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
