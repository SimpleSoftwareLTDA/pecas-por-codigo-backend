package org.pecasonline.features.items

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Int> {
    fun findByHash(hash: String): Item?
    fun findItemByDescriptionContains(description: String, pageable: Pageable): Page<Item>
    fun findItemByCode(code: String, pageable: Pageable): Page<Item>
}