package org.pecasonline.features.address.controller.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.address.domain.BrazilianState


@Tag(name = "Endereços", description = "Recursos relacionados a endereços, como estados e etc.")
interface AddressSwaggerSpec {

    @Operation(summary = "Buscar todas as marcas")
    @ApiResponse(
        responseCode = "200",
        description = "Marcas encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                   ExampleObject(
                        value = AddressExample.GET_STATES
                    )
                ]
                )
        ]
    )
    fun getBrazilianStates(): List<BrazilianState>
}