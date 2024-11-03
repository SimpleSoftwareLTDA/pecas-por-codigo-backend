package org.pecasonline.features.category

import org.springframework.data.domain.Page

interface ICategoryService {
    fun getAllCategories(page: Int?=0, size: Int?=10): Page<Category>
    fun getCategoryById(id: Int): Category
    fun searchCategory(name: String, page: Int?=0, size: Int?=10): Page<Category>
}