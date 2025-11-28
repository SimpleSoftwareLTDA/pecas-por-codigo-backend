package org.pecasonline.features.supplier.service

import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.springframework.data.domain.Page

interface ISupplierService {
    fun findSuppliers(page: Int? = 0, size: Int? = 10): Page<Supplier>
    fun findSupplierById(id: Int): Supplier
    fun findSupplierByCnpj(cnpj: String, page: Int? = 0, size: Int? = 10): Page<Supplier>
    fun createSupplier(supplier: CreateSupplierDTO): Supplier
    fun updateSupplier(id: Int, supplier: UpdateSupplierDTO): Supplier
    fun deleteSupplier(id: Int)
}
