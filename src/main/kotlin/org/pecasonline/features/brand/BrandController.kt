package org.pecasonline.features.brand

import io.micrometer.core.annotation.Timed
import org.pecasonline.common.Constants.BASE_ENDPOINT
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

    @Timed(value = "brands.get", description = "Time taken to return all brands")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getBrands(): List<Brand> = brand.getAvailableBrands()

}
