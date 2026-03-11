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

    @Test
    fun `should parse line with multiple spaces separating columns`() {
        // Mocking a line from PD2602.txt
        val line = "4C4513A350AA               1           0.00 INTERRUPTOR DE TESTE"
        val result = stockService.parseStockLine(line)
        
        assertNotNull(result)
        assertEquals("4C4513A350AA", result?.item?.code)
        assertEquals(1, result?.quantity)
        assertEquals(0L, result?.item?.priceInCents)
        assertEquals("INTERRUPTOR DE TESTE", result?.item?.description)
    }

    @Test
    fun `should handle empty descriptions correctly as fallback value`() {
        val line = "4C4513A350AA               1           0.00 "
        val result = stockService.parseStockLine(line)
        if (result != null) {
            assertEquals("4C4513A350AA", result.item.code)
            assertEquals("Sem descrição", result.item.description)
        }
    }

    // --- Happy Paths ---
    @Test
    fun `should parse semicolon delimited format`() {
        val line = "CODE123;10;19,99;Description with spaces"
        val result = stockService.parseStockLine(line)
        assertNotNull(result)
        assertEquals("CODE123", result?.item?.code)
        assertEquals(10, result?.quantity)
        assertEquals(1999L, result?.item?.priceInCents)
        assertEquals("Description with spaces", result?.item?.description)
    }

    @Test
    fun `should parse fractional quantities with dots and commas as thousands`() {
        // Quantities like 2.467 from PD2602 should be sanitized to 2467
        val line = "CODE123               2.467           10,50 DESC"
        val result = stockService.parseStockLine(line)
        assertNotNull(result)
        assertEquals(2467, result?.quantity)
        
        // Also test commas
        val lineComma = "CODE124               1,500           20.00 DESC2"
        val resultComma = stockService.parseStockLine(lineComma)
        assertNotNull(resultComma)
        assertEquals(1500, resultComma?.quantity)
    }

    @Test
    fun `should parse large prices with thousands separators`() {
        val lineUS = "CODE_US\t1\t1,005.47\tDesc"
        val lineBR = "CODE_BR\t1\t1.005,47\tDesc"
        
        val resUS = stockService.parseStockLine(lineUS)
        val resBR = stockService.parseStockLine(lineBR)
        
        assertEquals(100547L, resUS?.item?.priceInCents)
        assertEquals(100547L, resBR?.item?.priceInCents)
    }

    // --- Error Paths ---
    @Test
    fun `should return null for completely empty or blank lines`() {
        assertNull(stockService.parseStockLine(""))
        assertNull(stockService.parseStockLine("   \t  "))
    }

    @Test
    fun `should fallback to 0 quantity if quantity contains letters`() {
        // Regex allows digits, dots, commas. Letters won't match the third column properly in multiple spaces regex.
        // But if it's tab-separated, it splits directly.
        val line = "CODE\t10A\t10.00\tDesc"
        val result = stockService.parseStockLine(line)
        assertNotNull(result)
        assertEquals(0, result?.quantity) // "10A".toIntOrNull() -> null -> 0
    }

    // --- Exception Scenarios ---
    @Test
    fun `should not throw exception on missing price`() {
        val line = "CODE\t10\t\tDesc"
        val result = stockService.parseStockLine(line)
        assertNotNull(result)
        assertEquals(0L, result?.item?.priceInCents) // blank price -> 0L
    }

    @Test
    fun `should not throw exception on extremely large prices`() {
        // Price parsing catches NumberFormatException and defaults to 0
        val line = "CODE\t1\t999999999999999999999.99\tDesc"
        val result = stockService.parseStockLine(line)
        assertNotNull(result)
        assertEquals(0L, result?.item?.priceInCents)
    }
}
