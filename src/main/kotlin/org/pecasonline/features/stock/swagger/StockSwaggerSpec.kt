package org.pecasonline.features.stock.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.pecasonline.features.stock.Stock
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile

interface StockSwaggerSpec {

    @Operation(summary = "Retorna todos os estoques paginado")
    @ApiResponse(responseCode = "200",
        description = "Estoques retornados com sucesso",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_ALL_STOCKS
                    )
                ]
            )
        ]
    )
    fun stock(
        page: Int? = 0,
        size: Int? = 10
    ): Page<Stock>

    @Operation(summary = "Retorna um estoque pelo ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Estoque encontrado",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = StockExamples.GET_STOCK_BY_ID
                        )
                    ]
                )
            ]
        ),
        ApiResponse(responseCode = "404", description = "Estoque não encontrado",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            value = StockExamples.STOCK_NOT_FOUND
                        )
                    ]
                )
            ]
        ))
    fun findStockById(id: Int): Stock

    @Operation(summary = "Retorna um estoque pela id do item")
    @ApiResponse(responseCode = "200",
        description = "Estoques encontrados",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_STOCK_BY_ITEM_ID
                    )
                ]
            )
        ]
    )
    fun findStockByItemId(id: Int, page: Int? = 0, size: Int? = 10): Page<Stock>

    @Operation(summary = "Retorna um estoque pelo nome do fornecedor")
    @ApiResponse(responseCode = "200",
        description = "Estoques encontrados",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_STOCK_BY_SUPPLIER_NAME
                    )
                ]
            )
        ]
    )
    fun findStockBySupplierName(name: String, page: Int? = 0, size: Int? = 10): Page<Stock>

    @Operation(summary = "Retorna um estoque pelo ID do fornecedor")
    @ApiResponse(responseCode = "200",
        description = "Estoques encontrados",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_STOCK_BY_SUPPLIER_ID
                    )
                ]
            )
        ]
    )
    fun findStockBySupplierId(id: Int, page: Int? = 0, size: Int? = 10): Page<Stock>

    @Operation(summary = "Retorna um estoque pela descrição do item")
    @ApiResponse(responseCode = "200",
        description = "Estoques encontrados",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_STOCK_BY_DESCRIPTION
                    )
                ]
            )
        ]
    )
    fun findStockByItemDescription(description: String, page: Int? = 0, size: Int? = 10): Page<Stock>


    @Operation(summary = "Retorna um estoque pelo código do item")
    @ApiResponse(responseCode = "200",
        description = "Estoques encontrados",
        content = [
            Content(
                mediaType = "application/json",
                examples = [
                    ExampleObject(
                        value = StockExamples.GET_STOCK_BY_ITEM_CODE
                    )
                ]
            )
        ]
    )
    fun findStockByItemCode(code: String, page: Int? = 0, size: Int? = 10): Page<Stock>

    @Operation(summary = "Cria um estoque", description = "Recebe um arquivo tablado com os dados do estoque, sugiro nao esperar pela resposta pois demora bastante processar arquivos")
    @ApiResponse(responseCode = "201",
        description = "Estoque criado com sucesso",
        content = [Content(mediaType = "application/json")]
    )
    fun createItem(cnpj: String, file: MultipartFile)

}