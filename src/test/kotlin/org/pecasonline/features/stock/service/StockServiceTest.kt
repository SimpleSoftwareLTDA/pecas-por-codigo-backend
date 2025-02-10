package org.pecasonline.features.stock.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.verify
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.Stock
import org.pecasonline.features.stock.StockRepository
import org.pecasonline.features.stock.StockService
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.nio.file.Files
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class StockServiceTest {

    @MockBean
    private lateinit var stockRepository: StockRepository

    @MockBean
    private lateinit var itemRepository: ItemRepository

    @MockBean
    private lateinit var supplierRepository: SupplierRepository

    @MockBean
    private lateinit var categoryService: ICategoryService

    @Autowired
    private lateinit var stockService: StockService

    private lateinit var sampleCategory: Category
    private lateinit var sampleItem: Item
    private lateinit var sampleStock: Stock

    @BeforeEach
    fun setup() {
        sampleCategory = Category(id = 1, name = "Electronics")
        sampleItem = Item(id = 1, hash = "sampleHash", description = "Test Item", category = sampleCategory, code = "CODE")
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
        `when`(stockRepository.findAll(pageable)).thenReturn(pagedStocks)

        val result = stockService.getAllStocks(0, 2)

        assertEquals(1, result.totalElements)
        assertEquals(sampleStock, result.content[0])
        verify(stockRepository).findAll(pageable)
    }

    @Test
    fun `findStockById should return stock if found`() {
        `when`(stockRepository.findById(1)).thenReturn(Optional.of(sampleStock))

        val result = stockService.findStockById(1)

        assertEquals(sampleStock, result)
        verify(stockRepository).findById(1)
    }

    @Test
    fun `findStockById should throw NotFoundException if stock is not found`() {
        `when`(stockRepository.findById(99)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            stockService.findStockById(99)
        }
    }

    @Test
    fun `createStock should throw NotFoundException if supplier not found`() {
        val mockFile = createMockFile()
        val cnpj = "15826705000130"

        `when`(supplierRepository.findSuppliersByCnpj(cnpj)).thenReturn(emptyList())
        `when`(itemRepository.save(any(Item::class.java))).thenReturn(sampleItem) // Ensure itemRepository.save returns a non-null value
        `when`(categoryService.findByNameIgnoreCase(anyOrNull())).thenReturn(sampleCategory)
        assertThrows<NotFoundException> {
            stockService.createStock(mockFile, token = null)
        }
    }

    @Test
    fun `processItem should assign category and save item if not exists`() {
        `when`(itemRepository.findByHash("sampleHash")).thenReturn(null)
        `when`(categoryService.findByNameIgnoreCase(anyOrNull())).thenReturn(sampleCategory)
        `when`(itemRepository.save(any(Item::class.java))).thenReturn(sampleItem)

        val result = stockService.processItem(sampleItem)

        assertEquals(sampleItem, result)
        verify(itemRepository).save(any(Item::class.java))
    }
}
