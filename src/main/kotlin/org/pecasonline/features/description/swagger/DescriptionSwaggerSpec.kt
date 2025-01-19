package org.pecasonline.features.brand.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.description.Description

@Tag(name = "Descrições", description = "Recursos relacionados a descrições")
interface DescriptionSwaggerSpec {

    @Operation(summary = "Buscar todas descrições")
    @ApiResponse(
        responseCode = "200",
        description = "Descrições encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                   ExampleObject(
                        value = DescriptionExample.GET_DESCRIPTIONS
                    )
                ]
                )
        ]
    )
    fun getDescriptions(): List<Description>
}