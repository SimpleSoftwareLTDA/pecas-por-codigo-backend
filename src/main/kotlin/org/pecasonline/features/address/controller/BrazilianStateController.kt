package org.pecasonline.features.address.controller

import org.pecasonline.features.address.service.IStateService
import org.pecasonline.features.address.domain.BrazilianState
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class BrazilianStateController(
    private val stateService: IStateService
) {

    @GetMapping("/brazilian-states")
    fun getBrazilianStates(): List<BrazilianState> = stateService.getAvailableStates()
}