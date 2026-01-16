package org.pecasonline.features.supplier.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.pecasonline.features.supplier.service.ISupplierService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.Page
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(SupplierController::class)
@org.springframework.test.context.ActiveProfiles("test")
class SupplierControllerCnpjValidationTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var supplierService: ISupplierService

    private val unformattedCnpj = "04659416000177"
    private val formattedCnpj = "04.659.416/0001-77"
    private val invalidCnpj = "11.222.333/0001-99"

    @Test
    fun `should accept unformatted valid CNPJ in search`() {
        every { supplierService.findSupplierByCnpj(formattedCnpj, any(), any()) } returns Page.empty()

        mockMvc.get("/api/v1/fornecedores/cnpj") {
            param("cnpj", unformattedCnpj)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should accept formatted valid CNPJ in search`() {
        every { supplierService.findSupplierByCnpj(formattedCnpj, any(), any()) } returns Page.empty()

        mockMvc.get("/api/v1/fornecedores/cnpj") {
            param("cnpj", formattedCnpj)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `should reject invalid CNPJ in search`() {
        mockMvc.get("/api/v1/fornecedores/cnpj") {
            param("cnpj", invalidCnpj)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
