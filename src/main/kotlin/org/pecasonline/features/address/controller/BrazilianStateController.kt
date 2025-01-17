package org.pecasonline.features.address.controller

import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.address.controller.swagger.AddressSwaggerSpec
import org.pecasonline.features.address.service.IStateService
import org.pecasonline.features.address.domain.BrazilianState
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_ENDPOINT/estados")
class BrazilianStateController(
    private val stateService: IStateService
) : AddressSwaggerSpec {

    @GetMapping
    override fun getBrazilianStates(): List<BrazilianState> = stateService.getAvailableStates()
}