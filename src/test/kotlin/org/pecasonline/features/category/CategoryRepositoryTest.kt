package org.pecasonline.features.category

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@DataJpaTest
@Transactional
class CategoryRepositoryTest @Autowired constructor(
    private val categoryRepository: CategoryRepository
) {

    @BeforeEach
    fun setup() {
        categoryRepository.saveAll(listOf(
            Category(name = "TELA"),
            Category(name = "tela"),
            Category(name = "\"Mirror, O/S\""),
            Category(name = "mirror,o/s"),
            Category(name = "Retentor(SKF")
        ))
    }

    @Test
    fun `should format and deduplicate categories correctly`() {
        val categories = categoryRepository.findAll()

        val expectedNames = setOf("TELA", "\"Mirror, O/S\"", "Retentor(SKF", "tela", "mirror,o/s")
        val actualNames = categories.map { it.name }.toSet()

        assertEquals(expectedNames, actualNames)
    }

    @Test
    fun `should find categories by name ignoring case`() {
        val pageable: Pageable = PageRequest.of(0, 10)

        val result = categoryRepository.findByNameContainsIgnoreCase("mirror", pageable)

        assertTrue(result.content.any { it.name == "\"Mirror, O/S\"" })
        assertEquals(2, result.totalElements)
    }

    @Test
    fun `should paginate results correctly`() {
        val pageable: Pageable = PageRequest.of(0, 1)

        val result = categoryRepository.findAll(pageable)

        assertEquals(1, result.size)
        assertEquals(5, result.totalElements)
    }
}
