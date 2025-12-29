package org.pecasonline.features.supplier.service

import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.banking.BankingService
import org.pecasonline.common.httpclients.dto.CreateClientRequest
import org.pecasonline.features.address.service.IAddressService
import org.pecasonline.features.brand.IBrandService
import org.pecasonline.features.description.IDescriptionService
import org.pecasonline.features.subscription.service.ISubscriptionService
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.pecasonline.features.supplier.dto.SupplierResponseDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupplierService(
    private val supplierRepository: SupplierRepository,
    private val addressService: IAddressService,
    private val descriptionService: IDescriptionService,
    private val brandService: IBrandService,
    private val subscriptionService: ISubscriptionService,
    private val contactService: IContactService,
    private val bankingService: BankingService
): ISupplierService {
    override fun findSuppliers(page: Int?, size: Int?): Page<SupplierResponseDTO> {
        val pageable = PageRequest.of(page ?: 0, size ?: 10)
        val suppliers = supplierRepository.findAll(pageable)

        if(suppliers.isEmpty) throw NotFoundException("Nenhum fornecedor encontrado.")

        return suppliers.map { SupplierResponseDTO.fromEntity(it) }
    }

    override fun findSupplierById(id: Int): SupplierResponseDTO {
        val supplier = findSupplierEntityById(id)
        return SupplierResponseDTO.fromEntity(supplier)
    }

    override fun findSupplierByCnpj(cnpj: String, page: Int?, size: Int?): Page<SupplierResponseDTO> {
        val pageable = PageRequest.of(page ?: 0, size ?: 10)
        val suppliers = supplierRepository.findSupplierByCnpj(cnpj, pageable)
        
        if(suppliers.isEmpty) throw NotFoundException("Nenhum fornecedor encontrado.")

        return suppliers.map { SupplierResponseDTO.fromEntity(it) }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createSupplier(supplier: CreateSupplierDTO): SupplierResponseDTO {
        val existingSupplier = supplier.cnpj?.let { supplierRepository.findSuppliersByCnpj(it) }

        if (existingSupplier?.isNotEmpty() == true) {
            val supplierWithAsaasId = existingSupplier.find { it.asaasId != null }
            require(supplierWithAsaasId == null) { "Fornecedor com CNPJ ${supplier.cnpj} já cadastrado e vinculado ao Asaas." }
        }

        val savedContact = contactService.save(supplier.contact!!)
        val savedAddress = addressService.save(supplier.address!!)

        val newSupplier = supplier.toSupplier(
            contact = savedContact,
            address = savedAddress
        )

        val newlyCreatedCustomer = bankingService.createCustomer(
            CreateClientRequest(
                name = newSupplier.name,
                email = newSupplier.contact.stockEmail,
                phone = newSupplier.contact.whatsapp,
                cpfCnpj = newSupplier.cnpj
            )
        )

        val savedSupplier = supplierRepository.save(newSupplier.copy(asaasId = newlyCreatedCustomer.id))

        subscriptionService.createSubscription(supplier.subscription!!, savedSupplier)

        return SupplierResponseDTO.fromEntity(savedSupplier)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun updateSupplier(id: Int, supplier: UpdateSupplierDTO): SupplierResponseDTO {
        val existingSupplier = findSupplierEntityById(id)

        val updatedContact = supplier.contact?.let {
            contactService.update(existingSupplier.contact, it)
        } ?: existingSupplier.contact

        val updatedAddress = supplier.address?.let {
            addressService.update(existingSupplier.address, it)
        } ?: existingSupplier.address

        val updatedDescription = supplier.descriptionId?.let {
            descriptionService.findDescriptionById(it)
        } ?: existingSupplier.description

        val updatedSupplier = existingSupplier.copy(
            name = supplier.name ?: existingSupplier.name,
            socialName = supplier.socialName ?: existingSupplier.socialName,
            description = updatedDescription,
            contact = updatedContact,
            address = updatedAddress
        )

        val savedSupplier = supplierRepository.save(updatedSupplier)
        return SupplierResponseDTO.fromEntity(savedSupplier)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun deleteSupplier(id: Int) {
        val existingSupplier = findSupplierEntityById(id)
        supplierRepository.deleteById(existingSupplier.id!!)
    }

    private fun findSupplierEntityById(id: Int): Supplier {
        return supplierRepository.findById(id)
            .orElseThrow { NotFoundException("Fornecedor não encontrado.") }
    }
}
