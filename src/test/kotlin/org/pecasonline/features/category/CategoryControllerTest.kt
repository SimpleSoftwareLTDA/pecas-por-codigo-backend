package org.pecasonline.features.category

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CategoryController::class)
@org.springframework.test.context.ActiveProfiles("test")
class CategoryControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @MockkBean(relaxed = true)
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry

    @MockkBean
    private lateinit var categoryService: ICategoryService

    private val sampleCategories = listOf(
        Category(id = 1, name = "Tela"),
        Category(id = 2, name = "Mirror O S")
    )

    @Test
    fun `getAllCategories should return paginated categories with status OK`() {
        val pageable = PageRequest.of(0, 10)
        val pagedCategories = PageImpl(sampleCategories, pageable, sampleCategories.size.toLong())
        every { categoryService.getAllCategories(any(), any()) } returns pagedCategories

        mockMvc.perform(get("/api/v1/categorias")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].nome").value("Tela"))
            .andExpect(jsonPath("$.content[1].nome").value("Mirror O S"))

        verify { categoryService.getAllCategories(any(), any()) }
    }

    @Test
    fun `getCategoryById should return category with status OK`() {
        val categoryId = 1
        every { categoryService.getCategoryById(categoryId) } returns sampleCategories[0]

        mockMvc.perform(get("/api/v1/categorias/$categoryId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Tela"))

        verify { categoryService.getCategoryById(categoryId) }
    }

    @Test
    fun `getCategoryById should return status NOT_FOUND if category does not exist`() {
        val categoryId = 99
        every { categoryService.getCategoryById(categoryId) } throws NotFoundException("Category: $categoryId not found")

        mockMvc.perform(get("/api/v1/categorias/$categoryId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)

        verify { categoryService.getCategoryById(categoryId) }
    }

    @Test
    fun `getCategoryByName should return search results with status OK`() {
        val pageable = PageRequest.of(0, 10)
        val pagedCategories = PageImpl(listOf(sampleCategories[1]), pageable, 1)

        every { categoryService.searchCategory(any(), any(), any()) } returns pagedCategories

        mockMvc.perform(get("/api/v1/categorias/search")
            .param("name", "mirror")
            .param("page", "0")
            .param("size", "10")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content[0].nome").value("Mirror O S"))

        verify { categoryService.searchCategory(any(), any(), any()) }
    }
}
