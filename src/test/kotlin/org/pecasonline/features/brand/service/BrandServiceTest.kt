package org.pecasonline.features.brand.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandRepository
import org.pecasonline.features.brand.BrandService
import java.util.*

class BrandServiceTest {

    private val brandRepository: BrandRepository = mock()
    private val brandService = BrandService(brandRepository)

    @Test
    fun `should return all available brands`() {
        val brands = listOf(
            Brand(id = 1, brandName = "Toyota"),
            Brand(id = 2, brandName = "Honda")
        )
        
        whenever(brandRepository.findAll()).thenReturn(brands)

        val result = brandService.getAvailableBrands()

        assertEquals(2, result.size)
        assertEquals("Toyota", result[0].brandName)
        assertEquals("Honda", result[1].brandName)

        verify(brandRepository).findAll()
    }

    @Test
    fun `should return brand by id`() {
        val brand = Brand(id = 1, brandName = "Toyota")

        whenever(brandRepository.findById(1)).thenReturn(Optional.of(brand))

        val result = brandService.findBrandById(1)

        assertNotNull(result)
        assertEquals("Toyota", result.brandName)

        verify(brandRepository).findById(1)
    }

    @Test
    fun `should throw NotFoundException when brand id is not found`() {
        whenever(brandRepository.findById(99)).thenReturn(Optional.empty())

        val exception = assertThrows<NotFoundException> {
            brandService.findBrandById(99)
        }

        assertEquals("Marca não encontrada.", exception.message)
        verify(brandRepository).findById(99)
    }
}
