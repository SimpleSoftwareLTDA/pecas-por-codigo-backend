package org.pecasonline.features.brand.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandRepository
import org.pecasonline.features.brand.BrandService
import java.util.Optional

class BrandServiceTest {

    private val brandRepository: BrandRepository = mockk()
    private val brandService = BrandService(brandRepository)

    @Test
    fun `should return all available brands`() {
        val brands = listOf(
            Brand(id = 1, brandName = "Toyota"),
            Brand(id = 2, brandName = "Honda")
        )
        
        every { brandRepository.findAll() } returns brands

        val result = brandService.getAvailableBrands()

        assertEquals(2, result.size)
        assertEquals("Toyota", result[0].brandName)
        assertEquals("Honda", result[1].brandName)

        verify { brandRepository.findAll() }
    }

    @Test
    fun `should return brand by id`() {
        val brand = Brand(id = 1, brandName = "Toyota")

        every { brandRepository.findById(1) } returns Optional.of(brand)

        val result = brandService.findBrandById(1)

        assertNotNull(result)
        assertEquals("Toyota", result.brandName)

        verify { brandRepository.findById(1) }
    }

    @Test
    fun `should throw NotFoundException when brand id is not found`() {
        every { brandRepository.findById(99) } returns Optional.empty()

        val exception = assertThrows<NotFoundException> {
            brandService.findBrandById(99)
        }

        assertEquals("Marca não encontrada.", exception.message)
        verify { brandRepository.findById(99) }
    }
}
