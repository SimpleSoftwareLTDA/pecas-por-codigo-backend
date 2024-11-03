package org.pecasonline.features.category.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.category.Category
import org.springframework.data.domain.Page

@Tag(name = "Categoria", description = "Recursos relacionados a categorias")
interface CategorySwaggerSpec {

    @Operation(summary = "Buscar todas categorias")
    @ApiResponse(
        responseCode = "200",
        description = "Categorias encontradas",
        content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = CategoryExample.GET_CATEGORIES
                        )
                    ]
                )
        ]
    )
    fun getAllCategories(page: Int?=0, size: Int?=10): Page<Category>

    @Operation(summary = "Buscar categoria por id")
    @ApiResponse(
        responseCode = "200",
        description = "Categoria encontrada",
        content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = CategoryExample.GET_CATEGORY_BY_ID
                        )
                    ]
                )
        ]
    )
    fun getCategoryById(id: Int): Category

    @Operation(summary = "Buscar categoria por nome")
    @ApiResponse(
        responseCode = "200",
        description = "Categorias encontradas",
        content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = CategoryExample.GET_DESCRIPTION_BY_SEARCH_NAME
                        )
                    ]
                )
        ]
    )
    fun getCategoryByName(name: String, page: Int?=0, size: Int?=10): Page<Category>

}