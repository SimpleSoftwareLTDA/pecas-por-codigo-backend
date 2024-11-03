package org.pecasonline.features.category

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CategoryRepository : JpaRepository<Category, Int> {
    fun findByNameContainsIgnoreCase(name: String, pageable: Pageable): Page<Category>
    fun findByNameIgnoreCase(name: String): Category?
}