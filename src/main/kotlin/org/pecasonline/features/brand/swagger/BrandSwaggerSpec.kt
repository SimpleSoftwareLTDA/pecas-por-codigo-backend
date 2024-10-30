package org.pecasonline.features.brand.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.brand.Brand

@Tag(name = "Marcas", description = "Recursos relacionados a marcas")
interface BrandSwaggerSpec {

    @Operation(summary = "Buscar todas as marcas")
    @ApiResponse(
        responseCode = "200",
        description = "Marcas encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                   ExampleObject(
                        value = BrandExamples.GET_BRANDS
                    )
                ]
                )
        ]
    )
    fun getBrands(): List<Brand>
}