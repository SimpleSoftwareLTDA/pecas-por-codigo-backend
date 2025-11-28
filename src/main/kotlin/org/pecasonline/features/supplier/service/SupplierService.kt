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
import org.pecasonline.features.supplier.dto.UpdateContactDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.repository.ContactRepository
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
    private val contactRepository: ContactRepository,
    private val bankingService: BankingService
): ISupplierService {
    override fun findSuppliers(page: Int?, size: Int?): Page<Supplier> {
        val pageable = PageRequest.of(page!!, size!!)
        val suppliers = supplierRepository.findAll(pageable)

        if(suppliers.isEmpty) throw NotFoundException("Nenhum fornecedor encontrado.")

        return suppliers
    }

    override fun findSupplierById(id: Int): Supplier {
        val supplier = supplierRepository.findById(id)
        if(supplier.isEmpty) throw NotFoundException("Fornecedor não encontrado.")

        return supplier.get()
    }

    override fun findSupplierByCnpj(cnpj: String, page: Int?, size: Int?): Page<Supplier> {
        val pageable = PageRequest.of(page!!, size!!)
        val suppliers = supplierRepository.findSupplierByCnpj(cnpj, pageable)
        if(suppliers.isEmpty) throw NotFoundException("Nenhum fornecedor encontrado.")

        return suppliers
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun createSupplier(supplier: CreateSupplierDTO): Supplier {
        val existingSupplier = supplier.cnpj?.let { supplierRepository.findSuppliersByCnpj(it) }

        when {
            existingSupplier?.isNotEmpty() == true -> {
                val supplierWithAsaasId = existingSupplier.find { it.asaasId != null }

                require(supplierWithAsaasId == null) { "Fornecedor com CNPJ ${supplier.cnpj} já cadastrado e vinculado ao Asaas." }
            }
        }

        val savedContact = contactRepository.save(supplier.contact!!.toContact())
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

        return savedSupplier
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun updateSupplier(id: Int, supplier: UpdateSupplierDTO): Supplier {
        val existingSupplier = findSupplierById(id)

        val updatedContact = supplier.contact?.let {
            updateContact(existingSupplier.contact, it)
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

        return supplierRepository.save(updatedSupplier)
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun deleteSupplier(id: Int) {
        val existingSupplier = findSupplierById(id)
        supplierRepository.deleteById(existingSupplier.id!!)
    }

    private fun updateContact(existingContact: Contact, updatedContact: UpdateContactDTO): Contact {
        val contactToPersist = existingContact.copy(
            sellerName = updatedContact.sellerName ?: existingContact.sellerName,
            itemsEmail = updatedContact.itemsEmail ?: existingContact.itemsEmail,
            itemsPhone = updatedContact.itemsPhone ?: existingContact.itemsPhone,
            whatsapp = updatedContact.whatsapp ?: existingContact.whatsapp,
            itemsWhatsapp = updatedContact.itemsWhatsapp ?: existingContact.itemsWhatsapp,
            stockEmail = updatedContact.stockEmail ?: existingContact.stockEmail,
            billingEmail = updatedContact.billingEmail ?: existingContact.billingEmail,
            nfEmail = updatedContact.nfEmail ?: existingContact.nfEmail,
            site = updatedContact.site ?: existingContact.site
        )

        return contactRepository.save(contactToPersist)
    }
}
