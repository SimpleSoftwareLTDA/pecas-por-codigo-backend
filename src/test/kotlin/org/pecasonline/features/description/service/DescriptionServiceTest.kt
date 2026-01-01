package org.pecasonline.features.description.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionRepository
import org.pecasonline.features.description.DescriptionService
import java.util.Optional

class DescriptionServiceTest {

    private val descriptionRepository: DescriptionRepository = mockk()
    private val descriptionService = DescriptionService(descriptionRepository)

    @Test
    fun `should return all available descriptions`() {
        val descriptions = listOf(
            Description(id = 1, description = "Product description 1"),
            Description(id = 2, description = "Product description 2")
        )

        every { descriptionRepository.findAll() } returns descriptions

        val result = descriptionService.getAvailableDescriptions()

        assertEquals(2, result.size)
        assertEquals("Product description 1", result[0].description)
        assertEquals("Product description 2", result[1].description)

        verify { descriptionRepository.findAll() }
    }

    @Test
    fun `should return description by id`() {
        val description = Description(id = 1, description = "Product description")

        every { descriptionRepository.findById(1) } returns Optional.of(description)

        val result = descriptionService.findDescriptionById(1)

        assertNotNull(result)
        assertEquals("Product description", result.description)

        verify { descriptionRepository.findById(1) }
    }

    @Test
    fun `should throw NotFoundException when description id is not found`() {
        every { descriptionRepository.findById(99) } returns Optional.empty()

        val exception = assertThrows<NotFoundException> {
            descriptionService.findDescriptionById(99)
        }

        assertEquals("Descrição não encontrada.", exception.message)
        verify { descriptionRepository.findById(99) }
    }
}
