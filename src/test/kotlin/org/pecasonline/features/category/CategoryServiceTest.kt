package org.pecasonline.features.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class CategoryServiceTest @Autowired constructor(
    private val categoryService: CategoryService
) {

    @MockBean
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var sampleCategories: List<Category>

    @BeforeEach
    fun setup() {
        sampleCategories = listOf(
            Category(id = 1, name = "Tela"),
            Category(id = 2, name = "Mirror O S"),
            Category(id = 3, name = "Retentor Skf")
        )
    }

    @Test
    fun `getAllCategories should return paginated results`() {
        val pageable = PageRequest.of(0, 2)
        val pagedCategories = PageImpl(sampleCategories.subList(0, 2), pageable, sampleCategories.size.toLong())
        `when`(categoryRepository.findAll(pageable)).thenReturn(pagedCategories)

        val result = categoryService.getAllCategories(0, 2)

        assertEquals(2, result.size)
        assertEquals(3, result.totalElements)
        assertEquals("Tela", result.content[0].name)
        verify(categoryRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `getCategoryById should return category if found`() {
        val categoryId = 1
        val expectedCategory = sampleCategories[0]
        `when`(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory))

        val result = categoryService.getCategoryById(categoryId)

        assertEquals(expectedCategory, result)
        verify(categoryRepository, times(1)).findById(categoryId)
    }

    @Test
    fun `getCategoryById should throw NotFoundException if category not found`() {
        val categoryId = 99
        `when`(categoryRepository.findById(categoryId)).thenReturn(Optional.empty())

        val exception = assertThrows(NotFoundException::class.java) {
            categoryService.getCategoryById(categoryId)
        }
        assertEquals("Category: $categoryId not found", exception.message)
        verify(categoryRepository, times(1)).findById(categoryId)
    }

    @Test
    fun `searchCategory should return results ignoring case`() {
        val pageable = PageRequest.of(0, 10)
        val pagedCategories = PageImpl(sampleCategories.filter { it.name.contains("mirror", ignoreCase = true) }, pageable, 1)
        `when`(categoryRepository.findByNameContainsIgnoreCase("mirror", pageable)).thenReturn(pagedCategories)

        val result = categoryService.searchCategory("mirror", 0, 10)

        assertEquals(1, result.totalElements)
        assertEquals("Mirror O S", result.content[0].name)
        verify(categoryRepository, times(1)).findByNameContainsIgnoreCase("mirror", pageable)
    }

    @Test
    fun `addCategory should save a new category with formatted name`() {
        val category = Category(name = "new category")
        val formattedCategory = category.copy(name = "New Category")
        `when`(categoryRepository.findByNameContainsIgnoreCase("New Category", PageRequest.of(0, 1)))
            .thenReturn(PageImpl(emptyList()))
        `when`(categoryRepository.save(any(Category::class.java))).thenReturn(formattedCategory)

        val result = categoryService.addCategory(category)

        assertEquals("New Category", result.name)
        verify(categoryRepository).save(formattedCategory)
    }

    @Test
    fun `addCategory should not save duplicate category with different case or format`() {
        val duplicateCategory = Category(name = "mirror o s")
        val existingCategory = sampleCategories[1] // "Mirror O S"
        `when`(categoryRepository.findByNameContainsIgnoreCase("Mirror O S", PageRequest.of(0, 1)))
            .thenReturn(PageImpl(listOf(existingCategory)))

        val result = categoryService.addCategory(duplicateCategory)

        assertEquals(existingCategory.id, result.id)
        assertEquals(existingCategory.name, result.name)
        verify(categoryRepository, never()).save(any(Category::class.java))
    }

    @Test
    fun `addCategory should format name before saving`() {
        val categoryWithUnformattedName = Category(name = "\"tela\"")
        val formattedCategory = categoryWithUnformattedName.copy(name = "Tela")
        `when`(categoryRepository.findByNameContainsIgnoreCase("Tela", PageRequest.of(0, 1)))
            .thenReturn(PageImpl(emptyList()))
        `when`(categoryRepository.save(any(Category::class.java))).thenReturn(formattedCategory)

        val result = categoryService.addCategory(categoryWithUnformattedName)

        assertEquals("Tela", result.name)
        verify(categoryRepository).save(formattedCategory)
    }
}
