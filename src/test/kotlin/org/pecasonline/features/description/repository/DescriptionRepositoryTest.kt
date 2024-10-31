package org.pecasonline.features.description.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class DescriptionRepositoryTest @Autowired constructor(
    private val descriptionRepository: DescriptionRepository
) {

    @Test
    fun `should save and retrieve a description`() {
        val description = Description(description = "A brief description of the product.")
        val savedDescription = descriptionRepository.save(description)
        val retrievedDescription = descriptionRepository.findById(savedDescription.id!!)

        assertTrue(retrievedDescription.isPresent)
        assertEquals("A brief description of the product.", retrievedDescription.get().description)
    }

    @Test
    fun `should delete a description`() {
        val description = Description(description = "A removable description.")
        val savedDescription = descriptionRepository.save(description)
        descriptionRepository.deleteById(savedDescription.id!!)

        val retrievedDescription = descriptionRepository.findById(savedDescription.id!!)
        assertFalse(retrievedDescription.isPresent)
    }

    @Test
    fun `should find all descriptions`() {
        val description1 = Description(description = "First description.")
        val description2 = Description(description = "Second description.")
        descriptionRepository.saveAll(listOf(description1, description2))

        val descriptions = descriptionRepository.findAll()
        assertEquals(2, descriptions.size)
        assertTrue(descriptions.any { it.description == "First description." })
        assertTrue(descriptions.any { it.description == "Second description." })
    }
}
