package org.pecasonline.features.stock.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.Stock
import org.pecasonline.features.stock.StockRepository
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
class StockServiceTest {

    @MockkBean
    private lateinit var stockRepository: StockRepository

    @MockkBean
    private lateinit var itemRepository: ItemRepository

    @MockkBean
    private lateinit var supplierRepository: SupplierRepository

    @MockkBean
    private lateinit var categoryService: ICategoryService

    @Autowired
    private lateinit var stockService: org.pecasonline.features.stock.IStockService

    private lateinit var sampleCategory: Category
    private lateinit var sampleItem: Item
    private lateinit var sampleStock: Stock

    @BeforeEach
    fun setup() {
        sampleCategory = Category(id = 1, name = "Electronics")
        sampleItem = Item(id = 1, hash = "sampleHash", description = "Test Item", code = "CODE")
        sampleStock = Stock(id = 1, item = sampleItem, quantity = 100)
    }

    private fun createMockSupplier(cnpj: String) = Supplier(
        id = 1,
        cnpj = cnpj,
        name = "Test Supplier",
        address = Address(
            id = 1,
            street = "Test Street",
            city = "Test City",
            cep = "12345678",
            state = BrazilianState(1, "SP" ,"São Paulo")
        ),
        contact = Contact(
            sellerName = "Test Seller",
            itemsEmail = "itemsEmail@gmail.com",
            itemsPhone = "1234567890"
        ),
        socialName = "Test Social Name",
        stateSubscription = "Test State Subscription"
    )

    private fun createMockFile(content: String = "CODE 10 19.99 Test Item") =
        MockMultipartFile("file", "stocks.txt", "text/plain", content.toByteArray())

    @Test
    fun `getAllStocks should return paginated stock results`() {
        val pageable = PageRequest.of(0, 2)
        val pagedStocks = PageImpl(listOf(sampleStock), pageable, 1)
        every { stockRepository.findAll(pageable) } returns pagedStocks

        val result = stockService.getAllStocks(0, 2)

        assertEquals(1, result.totalElements)
        assertEquals(sampleStock, result.content[0])
        verify { stockRepository.findAll(pageable) }
    }

    @Test
    fun `findStockById should return stock if found`() {
        every { stockRepository.findById(1) } returns Optional.of(sampleStock)

        val result = stockService.findStockById(1)

        assertEquals(sampleStock, result)
        verify { stockRepository.findById(1) }
    }

    @Test
    fun `findStockById should throw NotFoundException if stock is not found`() {
        every { stockRepository.findById(99) } returns Optional.empty()

        assertThrows<NotFoundException> {
            stockService.findStockById(99)
        }
    }

    @Test
    fun `validateStockFile should auto-normalize comma separated files`() {
        val tempFile = java.io.File.createTempFile("test_comma", ".csv")
        tempFile.writeText("CODE1, 10, 15.00, Descricao teste\nCODE2, 5, 2.00, Outra peca")
        
        try {
            val result = stockService.validateStockFile(tempFile)
            assertEquals(2, result.validLinesCount)
            assertEquals(0, result.invalidLinesCount)
            assertEquals("CODE1", result.validLines[0].code)
            assertEquals(1500L, result.validLines[0].priceInCents)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `validateStockFile should auto-normalize pipe separated files`() {
        val tempFile = java.io.File.createTempFile("test_pipe", ".txt")
        tempFile.writeText("CODE1| 10 | 15.00 | Descricao teste\nCODE2| 5 | 2.00 | Outra peca")
        
        try {
            val result = stockService.validateStockFile(tempFile)
            assertEquals(2, result.validLinesCount)
            assertEquals(1500L, result.validLines[0].priceInCents)
            assertEquals(200L, result.validLines[1].priceInCents)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `validateStockFile should auto-normalize tab separated files`() {
        val tempFile = java.io.File.createTempFile("test_tab", ".tsv")
        tempFile.writeText("CODE1\t10\t1500\tDescricao teste\nCODE2\t5\t200\tOutra peca")
        
        try {
            val result = stockService.validateStockFile(tempFile)
            assertEquals(2, result.validLinesCount)
            assertEquals("CODE1", result.validLines[0].code)
            assertEquals("Descricao teste", result.validLines[0].description)
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `validateStockFile should handle separators inside quotes safely`() {
        val tempFile = java.io.File.createTempFile("test_quotes", ".csv")
        tempFile.writeText("CODE1, 10, \"1,500.00\", \"Descricao com virgula, teste\"\nCODE2, 5, 200, Outra peca")
        
        try {
            val result = stockService.validateStockFile(tempFile)
            assertEquals(2, result.validLinesCount)
            assertEquals("CODE1", result.validLines[0].code)
            assertEquals("Descricao com virgula, teste", result.validLines[0].description.replace("\"", ""))
        } finally {
            tempFile.delete()
        }
    }

    @Test

    fun `validateStockFile with real PD2602 file should pass`() {
        val resourceStream = this.javaClass.classLoader.getResourceAsStream("PD2602.txt")
            ?: throw IllegalStateException("Resource PD2602.txt not found")
        
        val tempFile = java.io.File.createTempFile("PD2602", ".txt")
        tempFile.outputStream().use { resourceStream.copyTo(it) }
        
        try {
            val result = stockService.validateStockFile(tempFile)
            
            println("=== Test Debug Info ===")
            println("Total lines: ${result.totalLines}")
            println("Valid lines: ${result.validLinesCount}")
            println("Invalid lines: ${result.invalidLinesCount}")
            
            if (result.invalidLinesCount > 1) {
                println("First 10 invalid lines:")
                result.invalidLines.take(10).forEach { println("[$it]") }
            }
            
            if (result.validLinesCount > 0) {
                println("First 5 valid lines:")
                result.validLines.take(5).forEach { println("[Code: ${it.code}, Qty: ${it.quantity}, Price: ${it.priceInCents}, Desc: ${it.description}]") }
            }
            
            // Assertions
            assertTrue(result.validLinesCount > 60000, "Should have processed most lines as valid. Found only ${result.validLinesCount}")
            assertEquals(1, result.invalidLinesCount, "Should have 1 invalid line (42534498) for this specific file. Found: ${result.invalidLines.joinToString(",")}")
            assertEquals("42534498", result.invalidLines[0].trim())
            
            // Verify first line: 4C4513A350AA               1           0.00 INTERRUPTOR
            val firstLine = result.validLines[0]
            assertEquals("4C4513A350AA", firstLine.code)
            assertEquals(1, firstLine.quantity)
            assertEquals(0L, firstLine.priceInCents)
            assertEquals("INTERRUPTOR", firstLine.description)
            
        } finally {

            tempFile.delete()
        }
    }
}

