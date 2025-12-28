package org.pecasonline.features.plan

import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.plan.swagger.PlanSwaggerSpec
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_ENDPOINT/planos")
class PlanController(
    private val planService: IPlanService,
    private val meterRegistry: io.micrometer.core.instrument.MeterRegistry
): PlanSwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getPlans(): List<Plan> = planService.getAvailablePlans().also {
        meterRegistry.counter("plan.list").increment()
    }
}