package org.pecasonline.features.brand

import org.pecasonline.features.Constants.Companion.BASE_ENDPOINT
import org.pecasonline.features.brand.swagger.BrandSwaggerSpec
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_ENDPOINT/marcas")
class BrandController(
    private val brand: IBrandService
): BrandSwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getBrands(): List<Brand> = brand.getAvailableBrands()

}