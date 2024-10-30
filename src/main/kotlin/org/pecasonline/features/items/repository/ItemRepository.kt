package org.pecasonline.features.items.repository

import org.pecasonline.features.items.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ItemRepository : JpaRepository<Item, Int> {
    fun findByHash(hash: String): Item?
}