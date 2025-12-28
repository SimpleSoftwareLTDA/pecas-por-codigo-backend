package org.pecasonline.features.supplier.controller
 
import jakarta.validation.Valid
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.supplier.controller.swagger.SupplierSwaggerSpec
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.dto.SupplierResponseDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.pecasonline.features.supplier.service.ISupplierService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RequestMapping("$BASE_ENDPOINT/fornecedores")
@RestController
class SupplierController(
    private val supplierService: ISupplierService,
    private val meterRegistry: io.micrometer.core.instrument.MeterRegistry
): SupplierSwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun suppliers(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): Page<SupplierResponseDTO> = supplierService.findSuppliers(page, size)

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun findSupplierById(
        @PathVariable("id") id: Int
    ): SupplierResponseDTO = supplierService.findSupplierById(id)


    @GetMapping("/cnpj")
    @ResponseStatus(HttpStatus.OK)
    override fun findSupplierByCnpj(
        @RequestParam("cnpj") cnpj: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ): Page<SupplierResponseDTO> = supplierService.findSupplierByCnpj(cnpj, page, size).also {
        meterRegistry.counter("supplier.search.cnpj", "cnpj", cnpj).increment()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    override fun createSupplier(
        @RequestBody @Valid supplier: CreateSupplierDTO
    ): SupplierResponseDTO = supplierService.createSupplier(supplier)

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun updateSupplier(
        @PathVariable("id") id: Int,
        @RequestBody @Valid supplier: UpdateSupplierDTO
    ): SupplierResponseDTO = supplierService.updateSupplier(id, supplier)

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    override fun deleteSupplier(
        @PathVariable("id") id: Int
    ) {
        supplierService.deleteSupplier(id)
    }
}
