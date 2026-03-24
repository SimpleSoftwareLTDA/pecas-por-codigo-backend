package org.pecasonline.features.stock.swagger

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.pecasonline.features.stock.Stock
import org.hibernate.validator.constraints.br.CNPJ
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
    fun findStockByItemDescription(descricao: String, page: Int? = 0, size: Int? = 10): Page<Stock>


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
    fun createItem(file: MultipartFile, token: String)

    @Operation(summary = "Cria um estoque por CNPJ")
    @ApiResponse(responseCode = "201",
        description = "Estoque sendo processado",
        content = [Content(mediaType = "application/json")]
    )
    fun createItemStockByCNPJ(file: MultipartFile, @CNPJ cnpj: String)

    @Operation(summary = "Valida um arquivo de estoque linha a linha e retorna o discriminativo das linhas (corretas/erradas)")
    @ApiResponse(responseCode = "200",
        description = "Arquivo processado e linhas validadas",
        content = [Content(mediaType = "application/json")]
    )
    fun validateStockFile(file: MultipartFile): org.pecasonline.features.stock.dto.StockValidationResult

    @Operation(summary = "Formata um arquivo de estoque extraindo as colunas especificadas (0-indexadas) e separando-as por ponto-e-vírgula")
    @ApiResponse(responseCode = "200",
        description = "Arquivo formatado e retornado para download",
        content = [Content(mediaType = "text/csv")]
    )
    fun formatStockFile(
        file: MultipartFile,
        codeCol: Int,
        qtyCol: Int,
        priceCol: Int,
        descCol: Int,
        delimiter: String
    ): org.springframework.http.ResponseEntity<org.springframework.core.io.Resource>

    @Operation(summary = "Retorna o histórico de envio de arquivos de estoque")
    @ApiResponse(responseCode = "200",
        description = "Histórico de envios de arquivos de estoque paginado",
        content = [Content(mediaType = "application/json")]
    )
    fun getUploadHistory(
        @Parameter(description = "CNPJ do Fornecedor") cnpj: String?,
        @Parameter(description = "Número da página") page: Int,
        @Parameter(description = "Tamanho da página") size: Int
    ): Page<org.pecasonline.features.stock.history.StockUploadHistoryDto>
}
