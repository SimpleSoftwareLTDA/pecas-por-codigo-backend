package org.pecasonline.features.category

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository
) : ICategoryService {
    override fun getAllCategories(page: Int?, size: Int?): Page<Category> {
        return categoryRepository.findAll(PageRequest.of(page!!, size!!))
    }

    override fun getCategoryById(id: Int): Category {
        return categoryRepository.findById(id).orElseThrow { NotFoundException("Category: $id not found") }
    }

    override fun searchCategory(name: String, page: Int?, size: Int?): Page<Category> {
        return categoryRepository.findByNameContainsIgnoreCase(name, PageRequest.of(page!!, size!!))
    }

    override fun findByNameIgnoreCase(name: String): Category? {
        return categoryRepository.findByNameIgnoreCase(name)
    }

    override fun addCategory(category: Category): Category {
        val formattedName = category.name.trim()
            .replace(Regex("^[^A-Za-z0-9]*|[^A-Za-z0-9]*$"), "")
            .replace(Regex("\\s+"), " ")
            .split(" ")
            .joinToString(" ") { it.lowercase().replaceFirstChar { char -> char.uppercase() } } // Capitalize each word

        val existingCategory = categoryRepository.findByNameContainsIgnoreCase(formattedName, PageRequest.of(0, 1))

        if (!existingCategory.isEmpty) return existingCategory.content[0]

        val newCategory = category.copy(name = formattedName)
        return categoryRepository.save(newCategory)
    }

}