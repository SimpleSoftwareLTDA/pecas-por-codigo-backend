package org.pecasonline.features.supplier.controller

import jakarta.validation.Valid
import org.pecasonline.features.Constants.Companion.BASE_ENDPOINT
import org.pecasonline.features.supplier.controller.swagger.SupplierSwaggerSpec
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.service.ISupplierService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RequestMapping("$BASE_ENDPOINT/fornecedores")
@RestController
class SupplierController(
    private val supplierService: ISupplierService,
): SupplierSwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun suppliers(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): Page<Supplier> = supplierService.findSuppliers(page, size)

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun findSupplierById(
        @PathVariable("id") id: Int
    ): Supplier = supplierService.findSupplierById(id)


    @GetMapping("/cnpj")
    @ResponseStatus(HttpStatus.OK)
    override fun findSupplierByCnpj(
        @RequestParam("cnpj") cnpj: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): Page<Supplier> = supplierService.findSupplierByCnpj(cnpj)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun createSupplier(
        @RequestBody @Valid supplier: CreateSupplierDTO
    ): Supplier = supplierService.createSupplier(supplier)


}