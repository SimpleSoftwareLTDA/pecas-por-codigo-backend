package org.pecasonline.features.stock.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.Stock
import org.pecasonline.features.stock.StockRepository
import org.pecasonline.features.stock.StockService
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockMultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@ExtendWith(MockitoExtension::class)
class StockServiceTest {


    @Mock
    private lateinit var stockRepository: StockRepository

    @Mock
    private lateinit var itemRepository: ItemRepository

    @Mock
    private lateinit var supplierRepository: SupplierRepository

    @Spy
    @InjectMocks
    private lateinit var stockService: StockService

    private lateinit var testSupplier: Supplier

    private lateinit var testItem: Item
    private lateinit var testStock: Stock

    @BeforeEach
    fun setUp() {
        testItem = Item.buildFromMinimalProperties("ITEM123", 1000L, "Sample Item")
        testSupplier = Supplier(
            name = "Supplier X",
            socialName = "Supplier X Ltd",
            cnpj = "12345678901234",
            stateSubscription = "123456",
            address = Address(
                street = "123 Main St",
                city = "Springfield",
                state = BrazilianState(stateName = "São Paulo", stateCode = "SP"),
                cep = "12345-678",
                country = "Brasil"
            ),
            contact = Contact(
                sellerName = "John Doe",
                itemsEmail = "items@example.com",
                itemsPhone = "555-1234",
                whatsapp = "555-5678",
                itemsWhatsapp = "555-8765",
                stockEmail = "stock@example.com",
                billingEmail = "billing@example.com",
                nfEmail = "nf@example.com",
                site = "www.supplierx.com"
            )
        )
        testStock = Stock(quantity = 10, supplier = testSupplier, item = testItem)
    }

    @Test
    fun `should return all stocks with pagination`() {
        val pageable = PageRequest.of(0, 10)
        whenever(stockRepository.findAll(pageable)) doReturn PageImpl(listOf(testStock))

        val result = stockService.getAllStocks(0, 10)

        assertEquals(1, result.totalElements)
        assertEquals(testStock, result.content[0])
    }

    @Test
    fun `should find stock by ID`() {
        whenever(stockRepository.findById(1)) doReturn Optional.of(testStock)

        val result = stockService.findStockById(1)

        assertEquals(testStock, result)
    }

    @Test
    fun `should throw NotFoundException if stock by ID does not exist`() {
        whenever(stockRepository.findById(1)) doReturn Optional.empty()

        assertThrows(NotFoundException::class.java) { stockService.findStockById(1) }
    }

    @Test
    fun `should find stock by item description ignoring case`() {
        val pageable = PageRequest.of(0, 10)
        whenever(stockRepository.findStockByItemDescriptionContainsIgnoreCase("sample", pageable)) doReturn PageImpl(
            listOf(testStock)
        )

        val result = stockService.findStockByItemDescription("sample", 0, 10)

        assertEquals(1, result.totalElements)
        assertEquals(testStock, result.content[0])
    }

    @Test
    fun `should find stock by item ID`() {
        val testAddress = Address(
            id = 1,
            street = "123 Main St",
            city = "Springfield",
            state = BrazilianState(stateName = "São Paulo", stateCode = "SP"),
            cep = "12345-678",
            country = "Brasil"
        )

        val testContact = Contact(
            id = 1,
            sellerName = "John Doe",
            itemsEmail = "supplier@example.com",
            itemsPhone = "555-1234"
        )

        val testItem = Item(
            id = 1,
            code = "ITEM123",
            description = "Sample Item",
            priceInCents = 1000L,
            hash = "hash123"
        )

        val testSupplier = Supplier(
            id = 1,
            name = "Supplier X",
            socialName = "Supplier X Ltd",
            cnpj = "12345678901234",
            stateSubscription = "123456",
            address = testAddress,
            contact = testContact
        )

        val stock = Stock(id = 1L, quantity = 15, item = testItem, supplier = testSupplier)

        whenever(stockRepository.findStockByItemId(eq(testItem.id!!), any())).thenReturn(PageImpl(listOf(stock)))

        val result = stockService.findStockByItemId(testItem.id!!, 0, 10)

        assertEquals(1, result.totalElements)
        assertEquals("Sample Item", result.content[0].item.description)
        assertEquals("Supplier X", result.content[0].supplier?.name)
    }


    @Test
    fun `should find stock by supplier name ignoring case`() {
        val pageable = PageRequest.of(0, 10)
        whenever(stockRepository.findStockBySupplierNameContainsIgnoreCase("supplier", pageable)) doReturn PageImpl(
            listOf(testStock)
        )

        val result = stockService.findStockBySupplierName("supplier", 0, 10)

        assertEquals(1, result.totalElements)
        assertEquals(testStock, result.content[0])
    }

    @Test
    fun `should handle RuntimeException during file cleanup in createStock`() {
        val mockFile = MockMultipartFile("file", "stocks.txt", "text/plain", "ITEM123 10 19.99 Sample Item".toByteArray())
        val tmpDir: Path = Files.createTempDirectory("tmp")

        doThrow(RuntimeException("Failed to delete file")).whenever(stockService).cleanupTempFiles(tmpDir)

        assertThrows(RuntimeException::class.java) {
            stockService.createStock("12345678901234", mockFile)
        }
    }

}
