package org.pecasonline.features.items

import org.springframework.data.domain.Page

interface IIitemService {
    fun getAllItems(page: Int?=0, size: Int?=10): Page<Item>
    fun findItemById(id: Int): Item
    fun findItemByDescription(descricao: String, page: Int?=0, size: Int?=10): Page<Item>
    fun findItemByCode(code: String, page: Int?=0, size: Int?=10): Page<Item>
}
