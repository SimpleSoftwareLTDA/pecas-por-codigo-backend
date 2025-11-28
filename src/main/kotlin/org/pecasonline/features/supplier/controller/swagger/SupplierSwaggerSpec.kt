package org.pecasonline.features.supplier.controller.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.springframework.data.domain.Page

@Tag(name = "Fornecedores", description = "Recursos relacionados a fornecedores")
interface SupplierSwaggerSpec {
    @Operation(summary = "Buscar todos os fornecedores com paginação")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Fornecedores encontrados", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.GET_ALL_SUPPLIERS_EXAMPLE)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
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
                    ExampleObject(value = SupplierExamples.GET_SUPPLIER_BY_ID)
                ])
        ]),
        ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
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
                    ExampleObject(value = SupplierExamples.GET_ALL_SUPPLIERS_EXAMPLE)
                ])
        ]),
        ApiResponse(responseCode = "404", description = "Fornecedor não encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
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
                examples = [
                    ExampleObject(value = SupplierExamples.CREATE_SUPPLIER_ANSWER)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                schema = Schema(implementation = Supplier::class),
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
                ])
        ]),
        ApiResponse(responseCode = "400", description = "Requisição inválida", content = [
            Content(mediaType = "application/json",
                schema = Schema(implementation = Supplier::class),
                examples = [
                    ExampleObject(value = SupplierExamples.BAD_REQUEST)
                ])
        ])
    ])
    @RequestBody(
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = CreateSupplierDTO::class),
            examples = [ExampleObject(value = SupplierExamples.CREATE_SUPPLIER_REQUEST)]
        )]
    )
    fun createSupplier(
        supplier: CreateSupplierDTO
    ): Supplier

    @Operation(summary = "Atualizar fornecedor")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Fornecedor atualizado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.GET_SUPPLIER_BY_ID)
                ])
        ]),
        ApiResponse(responseCode = "404", description = "Fornecedor nÇœo encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "400", description = "RequisiÇõÇœo invÇ­lida", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.BAD_REQUEST)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
                ])
        ])
    ])
    @RequestBody(
        content = [Content(
            mediaType = "application/json",
            schema = Schema(implementation = UpdateSupplierDTO::class),
            examples = [ExampleObject(value = SupplierExamples.UPDATE_SUPPLIER_REQUEST)]
        )]
    )
    fun updateSupplier(
        id: Int,
        supplier: UpdateSupplierDTO
    ): Supplier

    @Operation(summary = "Remover fornecedor")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Fornecedor removido"),
        ApiResponse(responseCode = "404", description = "Fornecedor nÇœo encontrado", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.SUPPLIER_NOT_FOUND)
                ])
        ]),
        ApiResponse(responseCode = "500", description = "Erro interno", content = [
            Content(mediaType = "application/json",
                examples = [
                    ExampleObject(value = SupplierExamples.INTERNAL_SERVER_ERROR)
                ])
        ])
    ])
    fun deleteSupplier(
        id: Int
    )
}
