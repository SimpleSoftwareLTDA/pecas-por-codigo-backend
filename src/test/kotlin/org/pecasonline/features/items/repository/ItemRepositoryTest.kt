package org.pecasonline.features.items.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class ItemRepositoryTest @Autowired constructor(
    private val itemRepository: ItemRepository
) {

    @BeforeEach
    fun setUp() {
        itemRepository.deleteAll()
    }

    @Test
    fun `should find item by hash`() {
        val item = Item.buildFromMinimalProperties(
            code = "ABC123",
            priceInCents = 1000L,
            description = "Test Item"
        )
        itemRepository.save(item)

        val retrievedItem = itemRepository.findByHash(item.hash)

        assertNotNull(retrievedItem)
        assertEquals(item.code, retrievedItem?.code)
    }

    @Test
    fun `should find items by description containing keyword ignoring case`() {
        val item1 = Item.buildFromMinimalProperties(
            code = "CODE1",
            priceInCents = 500L,
            description = "Amazing product"
        )
        val item2 = Item.buildFromMinimalProperties(
            code = "CODE2",
            priceInCents = 1500L,
            description = "Another great Product"
        )
        itemRepository.saveAll(listOf(item1, item2))

        val pageable = PageRequest.of(0, 10)
        val items = itemRepository.findItemByDescriptionContainsIgnoreCase("product", pageable)

        assertEquals(2, items.totalElements)
        assertTrue(items.content.any { it.code == "CODE1" })
        assertTrue(items.content.any { it.code == "CODE2" })
    }

    @Test
    fun `should find item by code`() {
        val item = Item.buildFromMinimalProperties(
            code = "UNIQUECODE",
            priceInCents = 2000L,
            description = "Unique product"
        )
        itemRepository.save(item)

        val pageable = PageRequest.of(0, 10)
        val items = itemRepository.findItemByCode("UNIQUECODE", pageable)

        assertEquals(1, items.totalElements)
        assertEquals("UNIQUECODE", items.content[0].code)
    }
}
