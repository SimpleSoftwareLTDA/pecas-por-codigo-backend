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
        val existingSupplier = supplier.cnpj?.let { supplierRepository.findSupplierByCnpj(it) }

        when {
            existingSupplier?.isNotEmpty() == true -> {
                val supplierWithAsaasId = existingSupplier.find { it.asaasId != null }

                require(supplierWithAsaasId == null) { "Fornecedor com CNPJ ${supplier.cnpj} já cadastrado e vinculado ao Asaas." }
            }
        }

        val savedContact = contactRepository.save(supplier.contact!!.toContact())
        val savedAddress = addressService.save(supplier.address!!)

        val chosenDescription = runCatching {
            descriptionService.findDescriptionById(supplier.descriptionId!!)
        }.getOrElse {
            throw IllegalArgumentException("A descrição escolhida não existe. descriptionId: ${supplier.descriptionId}")
        }

        val chosenBrand = runCatching {
            brandService.findBrandById(supplier.brandId!!)
        }.getOrElse {
            throw IllegalArgumentException("A marca escolhida não existe. brandId: ${supplier.brandId}")
        }

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
}