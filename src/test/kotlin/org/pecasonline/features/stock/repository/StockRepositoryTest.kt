package org.pecasonline.features.stock.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.items.Item
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.Stock
import org.pecasonline.features.stock.StockRepository
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class StockRepositoryTest @Autowired constructor(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository,
    private val stateRepository: BrazilianStatesRepository
) {

    lateinit var testItem: Item
    lateinit var testSupplier: Supplier

    @BeforeEach
    fun setUp() {
        stockRepository.deleteAll()

        val stateSP = stateRepository.save(
            BrazilianState(stateName = "São Paulo", stateCode = "SP")
        )
        val stateRJ = stateRepository.save(
            BrazilianState(stateName = "Rio de Janeiro", stateCode = "RJ")
        )

        val address1 = Address(
            street = "123 Main St",
            city = "Springfield",
            state = stateSP,
            cep = "12345-678",
            country = "Brasil"
        )

        val address2 = Address(
            street = "456 Market St",
            city = "Rio City",
            state = stateRJ,
            cep = "87654-321",
            country = "Brasil"
        )

        val contact1 = Contact(
            itemsEmail = "supplier@example.com",
            itemsPhone = "555-1234",
            sellerName = "John Doe"
        )

        val contact2 = Contact(
            itemsEmail = "super@example.com",
            itemsPhone = "555-9876",
            sellerName = "Jane Doe"
        )

        val supplier1 = Supplier(
            name = "Supplier X",
            supplierOriginalLink = "http://supplierx.com",
            socialName = "Supplier X Ltd",
            description = Description(description = "Description for Supplier X"),
            brand = Brand(brandName = "Brand X"),
            cnpj = "12345678901234",
            stateSubscription = "123456",
            address = address1,
            contact = contact1
        )

        val supplier2 = Supplier(
            name = "Superior Supplies",
            supplierOriginalLink = "http://superiorsupplies.com",
            socialName = "Superior Supplies Ltd",
            description = Description(description = "Description for Superior Supplies"),
            brand = Brand(brandName = "Brand Y"),
            cnpj = "98765432109876",
            stateSubscription = "654321",
            address = address2,
            contact = contact2
        )

        testItem = itemRepository.save(Item.buildFromMinimalProperties("ITEM123", 1000L, "Sample Item"))
        testSupplier = supplier1
        supplierRepository.saveAll(listOf(supplier1, supplier2))
    }

    @Test
    fun `should find stock by item description containing keyword ignoring case`() {
        val stock1 = Stock(quantity = 10, supplier = testSupplier, item = testItem)
        val stock2 = Stock(quantity = 20, supplier = testSupplier, item = testItem.copy(description = "Another Sample item"))
        stockRepository.saveAll(listOf(stock1, stock2))

        val pageable = PageRequest.of(0, 10)
        val result: Page<Stock> = stockRepository.findStockByItemDescriptionContainsIgnoreCase("sample", pageable)

        assertEquals(2, result.totalElements)
        assertTrue(result.content.any { it.item.description == "Sample Item" })
        assertTrue(result.content.any { it.item.description == "Another Sample item" })
    }

    @Test
    fun `should find stock by item id`() {
        val stock = Stock(quantity = 15, supplier = testSupplier, item = testItem)
        stockRepository.save(stock)

        val pageable = PageRequest.of(0, 10)
        val result = stockRepository.findStockByItemId(testItem.id!!, pageable)

        assertEquals(1, result.totalElements)
        assertEquals("Sample Item", result.content[0].item.description)
    }

    @Test
    fun `should find stock by item code`() {
        val stock = Stock(quantity = 15, supplier = testSupplier, item = testItem)
        stockRepository.save(stock)

        val pageable = PageRequest.of(0, 10)
        val result = stockRepository.findStockByItemCode("ITEM123", pageable)

        assertEquals(1, result.totalElements)
        assertEquals("ITEM123", result.content[0].item.code)
    }

    @Test
    fun `should find stock by supplier id`() {
        val stock = Stock(quantity = 15, supplier = testSupplier, item = testItem)
        stockRepository.save(stock)

        val pageable = PageRequest.of(0, 10)
        val result = stockRepository.findStockBySupplierId(testSupplier.id!!, pageable)

        assertEquals(1, result.totalElements)
        assertEquals("Supplier X", result.content[0].supplier?.name)
    }

    @Test
    fun `should find stock by supplier id and item id`() {
        val stock = Stock(quantity = 15, supplier = testSupplier, item = testItem)
        stockRepository.save(stock)

        val result = stockRepository.findStockBySupplierIdAndItemId(testSupplier.id!!, testItem.id!!)

        assertEquals(1, result.size)
        assertEquals("Sample Item", result[0].item.description)
    }
}
