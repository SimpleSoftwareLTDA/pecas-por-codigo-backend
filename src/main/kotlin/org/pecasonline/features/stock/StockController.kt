package org.pecasonline.features.stock

import org.pecasonline.features.Constants.Companion.BASE_ENDPOINT
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("$BASE_ENDPOINT/estoque")
class StockController(
    val stockService: IStockService
) {

    @GetMapping
    fun stock(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.getAllStocks(page, size)

    @GetMapping("/{id}")
    fun findStockById(
        @PathVariable("id") id: Int
    ) = stockService.findStockById(id)

    @GetMapping("/descricao")
    fun findStockByItemDescription(
        @RequestParam("descricao") descricao: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemDescription(descricao, page, size)

    @GetMapping("/item/{id}")
    fun findStockByItemId(
        @PathVariable("id") id: Int
    ) = stockService.findStockByItemId(id)

    @GetMapping("/codigo/{codigo}")
    fun findStockByItemCode(
        @PathVariable("codigo") codigo: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.finStockByItemCode(codigo, page, size)

    @GetMapping("/fornecedor/{id}")
    fun findStockBySupplierId(
        @PathVariable("id") id: Int,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierId(id, page, size)

    @GetMapping("/fornecedor")
    fun findStockBySupplierName(
        @RequestParam("nome") nome: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierName(nome, page, size)

    @PostMapping
    fun createItem(
        @RequestParam("cnpj") cnpj: String,
        @RequestParam("file") file: MultipartFile
    ) = stockService.createStock(cnpj, file)
}