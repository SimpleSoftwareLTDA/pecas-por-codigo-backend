package org.pecasonline.features.stock.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.items.Item
import org.pecasonline.features.stock.IStockService
import org.pecasonline.features.stock.Stock
import org.pecasonline.features.stock.StockController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(StockController::class)
@org.springframework.test.context.ActiveProfiles("test")
class StockControllerTest(@Autowired val mockMvc: MockMvc) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry


    @MockkBean
    private lateinit var stockService: IStockService

    @Test
    fun `should return paginated stock list`() {
        every { stockService.getAllStocks(0, 10) } returns PageImpl(emptyList())

        mockMvc.perform(get("/api/v1/estoque")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.getAllStocks(0, 10) }
    }

    @Test
    fun `should return stock by id`() {
        val stockId = 1
        val stock = Stock(id = 1, quantity = 10, supplier = null, item = Item(
            id = 1,
            code = "ITEM123",
            description = "Sample",
            hash = "hash",
        ))
        every { stockService.findStockById(stockId) } returns stock

        mockMvc.perform(get("/api/v1/estoque/$stockId"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stockId))

        verify { stockService.findStockById(stockId) }
    }

    @Test
    fun `should throw NotFoundException for non-existent stock`() {
        val stockId = 999
        every { stockService.findStockById(stockId) } throws NotFoundException("Estoque não encontrado")

        mockMvc.perform(get("/api/v1/estoque/$stockId"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Estoque não encontrado"))

        verify { stockService.findStockById(stockId) }
    }

    @Test
    fun `should search stock by item description`() {
        val description = "sample"
        every { stockService.findStockByItemDescription(description, 0, 10) } returns PageImpl(emptyList())

        mockMvc.perform(get("/api/v1/estoque/item")
                .param("descricao", description)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.findStockByItemDescription(description, 0, 10) }
    }

    @Test
    fun `should search stock by item ID`() {
        val itemId = 1
        every { stockService.findStockByItemId(itemId, 0, 10) } returns PageImpl(listOf(
            Stock(id = 1, quantity = 10, supplier = null, item = Item(
                id = 1,
                code = "ITEM123",
                description = "Sample",
                hash = "hash",
            ))
        ))

        mockMvc.perform(get("/api/v1/estoque/item/$itemId")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.findStockByItemId(itemId, 0, 10) }
    }

    @Test
    fun `should search stock by item code`() {
        val code = "ITEM123"
        every { stockService.findStockByItemCode(code, 0, 10) } returns PageImpl(emptyList())

        mockMvc.perform(get("/api/v1/estoque/codigo/$code")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.findStockByItemCode(code, 0, 10) }
    }

    @Test
    fun `should search stock by supplier ID`() {
        val supplierId = 1
        every { stockService.findStockBySupplierId(supplierId, 0, 10) } returns PageImpl(emptyList())

        mockMvc.perform(get("/api/v1/estoque/fornecedor/$supplierId")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.findStockBySupplierId(supplierId, 0, 10) }
    }

    @Test
    fun `should search stock by supplier name`() {
        val supplierName = "Supplier X"
        every { stockService.findStockBySupplierName(supplierName, 0, 10) } returns PageImpl(emptyList())

        mockMvc.perform(get("/api/v1/estoque/fornecedor")
                .param("nome", supplierName)
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify { stockService.findStockBySupplierName(supplierName, 0, 10) }
    }
}
