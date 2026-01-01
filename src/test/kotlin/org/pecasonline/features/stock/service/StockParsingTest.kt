package org.pecasonline.features.stock.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.pecasonline.features.stock.StockService
import io.mockk.mockk

class StockParsingTest {

    private val stockService = StockService(mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

    @Test
    fun `should detect scientific notation as invalid`() {
        val line = "7,90E+12\t10\t19.99\tDescription"
        val result = stockService.parseStockLine(line)
        assertNull(result, "Scientific notation should return null (invalid line)")
    }

    @Test
    fun `should parse valid line correctly`() {
        val line = "CODE123\t10\t19,99\tDescription"
        val result = stockService.parseStockLine(line)
        assertNotNull(result, "Valid line should be parsed")
    }

    @Test
    fun `should detect invalid column size`() {
        val line = "CODE123\t10\t19,99" // Only 3 columns
        val result = stockService.parseStockLine(line)
        assertNull(result, "Line with less than 4 columns should be invalid")
    }
}
