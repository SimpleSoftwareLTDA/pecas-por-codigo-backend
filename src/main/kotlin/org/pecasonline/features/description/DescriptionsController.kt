package org.pecasonline.features.description

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class DescriptionsController(
    private val descriptionService: IDescriptionService
) {

    @GetMapping("/descriptions")
    @ResponseStatus(HttpStatus.OK)
    fun getPlans(): List<Description> = descriptionService.getAvailableDescriptions()

}