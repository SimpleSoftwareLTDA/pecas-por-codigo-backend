package org.pecasonline.features.brand

interface IBrandService {
    fun getAvailableBrands(): List<Brand>
    fun findBrandById(id: Int): Brand
}