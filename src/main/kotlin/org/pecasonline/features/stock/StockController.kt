package org.pecasonline.features.stock

import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.stock.swagger.StockSwaggerSpec
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Validated
@RestController
@RequestMapping("$BASE_ENDPOINT/estoque")
@Tag(name = "Estoque", description = "Operações relacionadas ao estoque")
class StockController(
    val stockService: IStockService
): StockSwaggerSpec {

    @GetMapping
    override fun stock(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.getAllStocks(page, size)

    @GetMapping("/{id}")
    override fun findStockById(
        @PathVariable("id") id: Int
    ) = stockService.findStockById(id)

    @GetMapping("/item")
    override fun findStockByItemDescription(
        @RequestParam("descricao") descricao: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemDescription(descricao, page, size)

    @GetMapping("/item/{id}")
    override fun findStockByItemId(
        @PathVariable("id") id: Int,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemId(id, page, size)

    @GetMapping("/codigo/{codigo}")
    override fun findStockByItemCode(
        @PathVariable("codigo") code: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemCode(code, page, size)

    @GetMapping("/fornecedor/{id}")
    override fun findStockBySupplierId(
        @PathVariable("id") id: Int,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierId(id, page, size)

    @GetMapping("/fornecedor")
    override fun findStockBySupplierName(
        @RequestParam("nome") name: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierName(name, page, size)

    @PostMapping(consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    override fun createItem(
        @RequestPart file: MultipartFile,
        @RequestParam token: String
    ) {
        MDC.putCloseable("token", token).use {
            MDC.putCloseable("tid", UUID.randomUUID().toString()).use {
                stockService.createStock(file = file, token = token)
            }
        }
    }
}