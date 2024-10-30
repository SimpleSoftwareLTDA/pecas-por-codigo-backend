package org.pecasonline.features.description

import org.pecasonline.features.Constants.Companion.BASE_ENDPOINT
import org.pecasonline.features.brand.swagger.DescriptionSwaggerSpec
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_ENDPOINT/descricoes")
class DescriptionsController(
    private val descriptionService: IDescriptionService
): DescriptionSwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getDescriptions(): List<Description> = descriptionService.getAvailableDescriptions()

}