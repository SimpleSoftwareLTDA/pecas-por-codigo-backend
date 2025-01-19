package org.pecasonline.features.plan.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.plan.Plan

@Tag(name = "Planos", description = "Recursos relacionados a planos")
interface PlanSwaggerSpec {

    @Operation(summary = "Buscar todas descrições")
    @ApiResponse(
        responseCode = "200",
        description = "Descrições encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                   ExampleObject(
                        value = PlanExample.GET_PLANS
                    )
                ]
                )
        ]
    )
    fun getPlans(): List<Plan>
}