package org.pecasonline.features.items.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.items.ItemService
import org.pecasonline.features.items.ItemsMetricsService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional

class ItemServiceTest {

    private val itemRepository: ItemRepository = mockk()
    private val metrics: ItemsMetricsService = mockk(relaxed = true)
    private val itemService = ItemService(itemRepository, metrics = metrics)

    @Test
    fun `should return paginated list of all items`() {
        val items = listOf(
            Item.buildFromMinimalProperties(code = "ABC123", priceInCents = 1000L, description = "Test Item 1"),
            Item.buildFromMinimalProperties(code = "XYZ456", priceInCents = 2000L, description = "Test Item 2")
        )
        val pageRequest = PageRequest.of(0, 10)
        every { itemRepository.findAll(pageRequest) } returns PageImpl(items, pageRequest, items.size.toLong())

        val result = itemService.getAllItems(0, 10)

        assertEquals(2, result.content.size)
        assertEquals("ABC123", result.content[0].code)
        assertEquals("XYZ456", result.content[1].code)

        verify { itemRepository.findAll(pageRequest) }
    }

    @Test
    fun `should find item by id`() {
        val item = Item.buildFromMinimalProperties(code = "ABC123", priceInCents = 1000L, description = "Test Item")
        every { itemRepository.findById(1) } returns Optional.of(item)

        val result = itemService.findItemById(1)

        assertNotNull(result)
        assertEquals("ABC123", result.code)

        verify { itemRepository.findById(1) }
    }

    @Test
    fun `should throw NotFoundException when item id is not found`() {
        every { itemRepository.findById(99) } returns Optional.empty()

        val exception = assertThrows<NotFoundException> {
            itemService.findItemById(99)
        }

        assertEquals("Peça não encontrada", exception.message)
        verify { itemRepository.findById(99) }
    }

    @Test
    fun `should find items by description containing keyword`() {
        val items = listOf(
            Item.buildFromMinimalProperties(code = "ABC123", priceInCents = 1000L, description = "Test Item 1"),
            Item.buildFromMinimalProperties(code = "XYZ456", priceInCents = 2000L, description = "Another test item")
        )
        val pageRequest = PageRequest.of(0, 10)
        every { itemRepository.findItemByDescriptionContainsIgnoreCase("test", pageRequest) } returns PageImpl(items, pageRequest, items.size.toLong())

        val result = itemService.findItemByDescription("test", 0, 10)

        assertEquals(2, result.content.size)
        assertTrue(result.content.any { it.code == "ABC123" })
        assertTrue(result.content.any { it.code == "XYZ456" })

        verify { itemRepository.findItemByDescriptionContainsIgnoreCase("test", pageRequest) }
    }

    @Test
    fun `should find item by code`() {
        val items = listOf(
            Item.buildFromMinimalProperties(code = "ABC123", priceInCents = 1000L, description = "Test Item")
        )
        val pageRequest = PageRequest.of(0, 10)
        every { itemRepository.findItemByCode("ABC123", pageRequest) } returns PageImpl(items, pageRequest, items.size.toLong())

        val result = itemService.findItemByCode("ABC123", 0, 10)

        assertEquals(1, result.content.size)
        assertEquals("ABC123", result.content[0].code)

        verify { itemRepository.findItemByCode("ABC123", pageRequest) }
    }
}
