package org.pecasonline.features.description.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionRepository
import org.pecasonline.features.description.DescriptionService
import java.util.*

class DescriptionServiceTest {

    private val descriptionRepository: DescriptionRepository = mock()
    private val descriptionService = DescriptionService(descriptionRepository)

    @Test
    fun `should return all available descriptions`() {
        val descriptions = listOf(
            Description(id = 1, description = "Product description 1"),
            Description(id = 2, description = "Product description 2")
        )

        whenever(descriptionRepository.findAll()).thenReturn(descriptions)

        val result = descriptionService.getAvailableDescriptions()

        assertEquals(2, result.size)
        assertEquals("Product description 1", result[0].description)
        assertEquals("Product description 2", result[1].description)

        verify(descriptionRepository).findAll()
    }

    @Test
    fun `should return description by id`() {
        val description = Description(id = 1, description = "Product description")

        whenever(descriptionRepository.findById(1)).thenReturn(Optional.of(description))

        val result = descriptionService.findDescriptionById(1)

        assertNotNull(result)
        assertEquals("Product description", result.description)

        verify(descriptionRepository).findById(1)
    }

    @Test
    fun `should throw NotFoundException when description id is not found`() {
        whenever(descriptionRepository.findById(99)).thenReturn(Optional.empty())

        val exception = assertThrows<NotFoundException> {
            descriptionService.findDescriptionById(99)
        }

        assertEquals("Descrição não encontrada.", exception.message)
        verify(descriptionRepository).findById(99)
    }
}
