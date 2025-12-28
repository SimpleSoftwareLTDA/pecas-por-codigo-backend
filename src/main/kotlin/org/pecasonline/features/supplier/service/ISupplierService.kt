package org.pecasonline.features.supplier.service

import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.dto.SupplierResponseDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.springframework.data.domain.Page

interface ISupplierService {
    fun findSuppliers(page: Int? = 0, size: Int? = 10): Page<SupplierResponseDTO>
    fun findSupplierById(id: Int): SupplierResponseDTO
    fun findSupplierByCnpj(cnpj: String, page: Int? = 0, size: Int? = 10): Page<SupplierResponseDTO>
    fun createSupplier(supplier: CreateSupplierDTO): SupplierResponseDTO
    fun updateSupplier(id: Int, supplier: UpdateSupplierDTO): SupplierResponseDTO
    fun deleteSupplier(id: Int)
}
