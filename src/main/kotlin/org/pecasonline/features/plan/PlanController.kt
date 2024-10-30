package org.pecasonline.features.plan

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class PlanController(
    private val planService: IPlanService
) {

    @GetMapping("/plans")
    @ResponseStatus(HttpStatus.OK)
    fun getPlans(): List<Plan> = planService.getAvailablePlans()
}