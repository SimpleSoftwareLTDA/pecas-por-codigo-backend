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


}