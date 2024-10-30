package org.pecasonline.features.brand

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class BrandService(
    private val brandRepository: BrandRepository,
) : IBrandService {

    override fun getAvailableBrands(): List<Brand> = brandRepository.findAll()

    override fun findBrandById(id: Int): Brand = brandRepository.findById(id)
        .orElseThrow { NotFoundException("Marca não encontrada.") }
}