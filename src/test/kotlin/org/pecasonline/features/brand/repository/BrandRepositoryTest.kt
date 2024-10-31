package org.pecasonline.features.brand.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class BrandRepositoryTest @Autowired constructor(
    private val brandRepository: BrandRepository
) {

    @Test
    fun `should save and retrieve a brand`() {
        val brand = Brand(brandName = "Toyota")
        val savedBrand = brandRepository.save(brand)
        val retrievedBrand = brandRepository.findById(savedBrand.id!!)

        assertTrue(retrievedBrand.isPresent)
        assertEquals("Toyota", retrievedBrand.get().brandName)
    }

    @Test
    fun `should delete a brand`() {
        val brand = Brand(brandName = "Honda")
        val savedBrand = brandRepository.save(brand)
        brandRepository.deleteById(savedBrand.id!!)

        val retrievedBrand = brandRepository.findById(savedBrand.id!!)
        assertFalse(retrievedBrand.isPresent)
    }

    @Test
    fun `should find all brands`() {
        val brand1 = Brand(brandName = "Ford")
        val brand2 = Brand(brandName = "Chevrolet")
        brandRepository.saveAll(listOf(brand1, brand2))

        val brands = brandRepository.findAll()
        assertEquals(2, brands.size)
        assertTrue(brands.any { it.brandName == "Ford" })
        assertTrue(brands.any { it.brandName == "Chevrolet" })
    }
}
