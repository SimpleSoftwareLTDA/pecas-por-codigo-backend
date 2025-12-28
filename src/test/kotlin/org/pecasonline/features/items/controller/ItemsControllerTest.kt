package org.pecasonline.features.items.controller

import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.kotlin.*
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.items.IIitemService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemsController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(ItemsController::class)
@org.springframework.test.context.ActiveProfiles("test")
class ItemsControllerTest @Autowired constructor(private val mockMvc: MockMvc) {

    @MockBean
    private lateinit var itemsService: IIitemService

    @MockBean(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @Test
    fun `should return paginated list of all items`() {
        val items = listOf(
            Item.buildFromMinimalProperties("ABC123", 1000L, "Item 1"),
            Item.buildFromMinimalProperties("XYZ456", 2000L, "Item 2")
        )
        val pageRequest = PageRequest.of(0, 10)
        whenever(itemsService.getAllItems(0, 10)).thenReturn(PageImpl(items, pageRequest, items.size.toLong()))

        mockMvc.get("/api/v1/pecas?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content[0].codigo") { value("ABC123") }
                jsonPath("$.content[1].codigo") { value("XYZ456") }
            }

        verify(itemsService).getAllItems(0, 10)
    }

    @Test
    fun `should find item by id`() {
        val item = Item.buildFromMinimalProperties("ABC123", 1000L, "Item Description")
        whenever(itemsService.findItemById(1)).thenReturn(item)

        mockMvc.get("/api/v1/pecas/1")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.codigo") { value("ABC123") }
            }

        verify(itemsService).findItemById(1)
    }

    @Test
    fun `should return 404 when item id is not found`() {
        whenever(itemsService.findItemById(99)).thenThrow(NotFoundException("Peça não encontrada"))

        mockMvc.get("/api/v1/pecas/99")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.message") { value("Peça não encontrada") }
            }

        verify(itemsService).findItemById(99)
    }

    @Test
    fun `should find items by description`() {
        val items = listOf(
            Item.buildFromMinimalProperties("ABC123", 1000L, "Item 1"),
            Item.buildFromMinimalProperties("XYZ456", 2000L, "Another item")
        )
        val pageRequest = PageRequest.of(0, 10)
        whenever(itemsService.findItemByDescription("item", 0, 10)).thenReturn(PageImpl(items, pageRequest, items.size.toLong()))

        mockMvc.get("/api/v1/pecas/descricao?descricao=item&page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content[0].codigo") { value("ABC123") }
                jsonPath("$.content[1].codigo") { value("XYZ456") }
            }

        verify(itemsService).findItemByDescription("item", 0, 10)
    }

    @Test
    fun `should find items by code`() {
        val items = listOf(
            Item.buildFromMinimalProperties("ABC123", 1000L, "Item with code ABC123")
        )
        val pageRequest = PageRequest.of(0, 10)
        whenever(itemsService.findItemByCode("ABC123", 0, 10)).thenReturn(PageImpl(items, pageRequest, items.size.toLong()))

        mockMvc.get("/api/v1/pecas/codigo/ABC123?page=0&size=10")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.content[0].codigo") { value("ABC123") }
            }

        verify(itemsService).findItemByCode("ABC123", 0, 10)
    }
}
