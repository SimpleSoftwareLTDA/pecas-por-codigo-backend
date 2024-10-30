package org.pecasonline.features.supplier.controller.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus

interface SwaggerSpec {
    @Operation(summary = "Buscar todos os fornecedores com paginação")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Fornecedores encontrados", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.GET_ALL_SUPPLIERS_EXAMPLE)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.INTERNAL_SERVER_ERROR)
                ])
    ])])
    fun suppliers(
        page: Int? = 0,
        size: Int? = 10
    ): Page<Supplier>

    @Operation(summary = "Buscar fornecedor por ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Fornecedor encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.GET_ALL_SUPPLIERS_EXAMPLE)
                ])
        ]),
        ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.INTERNAL_SERVER_ERROR)
                ])
        ])
    ])
    fun findSupplierById(
        id: Int
    ): Supplier

    @Operation(summary = "Buscar fornecedor por CNPJ")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Fornecedor encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.GET_ALL_SUPPLIERS_EXAMPLE)
                ])
        ]),
        ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = Examples.INTERNAL_SERVER_ERROR)
                ])
        ])
    ])
    fun findSupplierByCnpj(
        cnpj: String,
        page: Int? = 0,
        size: Int? = 10
    ): Page<Supplier>

    @Operation(summary = "Criar fornecedor")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Fornecedor criado", content = [
            Content(mediaType = "application/json",
                schema = Schema(implementation = Supplier::class),
                examples = [
                    ExampleObject(value = Examples.CREATE_SUPPLIER)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                schema = Schema(implementation = Supplier::class),
                examples = [
                    ExampleObject(value = Examples.INTERNAL_SERVER_ERROR)
                ])
        ]),
        ApiResponse(responseCode = "400", description = "Requisição inválida", content = [
            Content(mediaType = "application/json",
                schema = Schema(implementation = Supplier::class),
                examples = [
                    ExampleObject(value = Examples.BAD_REQUEST)
                ])
        ])

    ])
    fun createSupplier(
        supplier: CreateSupplierDTO
    ): Supplier
}