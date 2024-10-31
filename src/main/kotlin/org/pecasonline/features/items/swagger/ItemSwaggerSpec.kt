package org.pecasonline.features.items.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.pecasonline.features.items.Item
import org.springframework.data.domain.Page

interface ItemSwaggerSpec {
    /*

    @GetMapping("/codigo/{codigo}")
    fun findItemByCode(
        @PathVariable("codigo") codigo: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.findItemByCode(codigo, page, size)
     */

    @Operation(summary = "Retorna todas as peças")
    @ApiResponse(responseCode = "200",
        description = "Peças retornadas com sucesso",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = ItemExample.GET_ALL_ITEMS
                    )
                ]
            )
        ]
    )
    fun items(
        page: Int? = 0,
        size: Int? = 10
    ): Page<Item>

    @Operation(summary = "Retorna uma peça pelo ID")
    @ApiResponse(responseCode = "200",
        description = "Peça encontrada",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = ItemExample.GET_ITEM_BY_ID
                    )
                ]
            )
        ]
    )
    fun findItemById(id: Int): Item

    @Operation(summary = "Retorna uma peça pela descrição")
    @ApiResponse(responseCode = "200",
        description = "Peças encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = ItemExample.GET_ITEM_BY_DESCRIPTION
                    )
                ]
            )
        ]
    )
    fun findItemByDescription(descricao: String, page: Int? = 0, size: Int? = 10): Page<Item>

    @Operation(summary = "Retorna uma peça pelo código")
    @ApiResponse(responseCode = "200",
        description = "Peças encontradas",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = ItemExample.GET_ITEM_BY_CODE
                    )
                ]
            )
        ]
    )
    fun findItemByCode(codigo: String, page: Int? = 0, size: Int? = 10): Page<Item>
}