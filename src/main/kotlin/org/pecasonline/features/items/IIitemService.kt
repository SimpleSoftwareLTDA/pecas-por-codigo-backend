package org.pecasonline.features.items

import org.springframework.data.domain.Page

interface IIitemService {
    fun getAllItems(page: Int?=0, size: Int?=10): Page<Item>
    fun findItemById(id: Int): Item
    fun findItemByDescription(descricao: String, page: Int?=0, size: Int?=10): Page<Item>
    fun findItemByCode(code: String, page: Int?=0, size: Int?=10): Page<Item>
}

fun generateListWithDuplicates(): List<Int> {
    val originalItems = (1..80).toList() // 80 itens únicos
    val duplicates = (1..20).toList() // 20 itens para duplicar
    return (originalItems + duplicates).shuffled() // Mistura os itens para simular duplicados
}

fun main() {
    val listWithDuplicates = generateListWithDuplicates()
    println(listWithDuplicates)
}
