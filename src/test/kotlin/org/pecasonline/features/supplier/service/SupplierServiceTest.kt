package org.pecasonline.features.supplier.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.dto.CreateAddressDTO
import org.pecasonline.features.address.dto.UpdateAddressDTO
import org.pecasonline.features.address.service.IAddressService
import org.pecasonline.features.banking.BankingService
import org.pecasonline.features.brand.IBrandService
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.IDescriptionService
import org.pecasonline.features.subscription.service.ISubscriptionService
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.dto.CreateContactDTO
import org.pecasonline.features.supplier.dto.UpdateContactDTO
import org.pecasonline.features.supplier.dto.UpdateSupplierDTO
import org.pecasonline.features.supplier.repository.ContactRepository
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

class SupplierServiceTest {

    @Mock
    private lateinit var supplierRepository: SupplierRepository

    @Mock
    private lateinit var addressService: IAddressService

    @Mock
    private lateinit var descriptionService: IDescriptionService

    @Mock
    private lateinit var brandService: IBrandService

    @Mock
    private lateinit var subscriptionService: ISubscriptionService

    @Mock
    private lateinit var contactRepository: ContactRepository

    @Mock
    private lateinit var bankingService: BankingService

    @InjectMocks
    private lateinit var supplierService: SupplierService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should find suppliers successfully`() {
        val supplier = buildSupplier()
        val pageRequest = PageRequest.of(0, 10)
        val suppliersPage: Page<Supplier> = PageImpl(listOf(supplier))

        whenever(supplierRepository.findAll(pageRequest)).thenReturn(suppliersPage)

        val result = supplierService.findSuppliers(0, 10)

        assertEquals(1, result.content.size)
        assertEquals(supplier, result.content[0])
    }

    @Test
    fun `should throw NotFoundException when no suppliers found`() {
        val pageRequest = PageRequest.of(0, 10)
        val emptyPage: Page<Supplier> = PageImpl(emptyList())

        whenever(supplierRepository.findAll(pageRequest)).thenReturn(emptyPage)

        assertThrows<NotFoundException> {
            supplierService.findSuppliers(0, 10)
        }
    }

    @Test
    fun `should find supplier by id`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        val result = supplierService.findSupplierById(supplier.id!!)

        assertEquals(supplier, result)
    }

    @Test
    fun `should throw NotFoundException when supplier by id not found`() {
        whenever(supplierRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            supplierService.findSupplierById(999)
        }
    }

    @Test
    fun `should update supplier basic fields`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        val updatedSupplier = supplier.copy(name = "Updated Supplier", socialName = "Updated Social Name")
        whenever(supplierRepository.save(any())).thenReturn(updatedSupplier)

        val dto = UpdateSupplierDTO(
            name = "Updated Supplier",
            socialName = "Updated Social Name"
        )

        val result = supplierService.updateSupplier(supplier.id!!, dto)

        assertEquals("Updated Supplier", result.name)
        assertEquals("Updated Social Name", result.socialName)
        verify(supplierRepository).save(any())
    }

    @Test
    fun `should update supplier contact`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        val updatedContact = supplier.contact.copy(
            sellerName = "Maria Souza",
            itemsEmail = "maria.souza@example.com",
            itemsPhone = "11999999999"
        )
        whenever(contactRepository.save(any())).thenReturn(updatedContact)
        whenever(supplierRepository.save(any())).thenReturn(supplier.copy(contact = updatedContact))

        val dto = UpdateSupplierDTO(
            contact = UpdateContactDTO(
                sellerName = "Maria Souza",
                itemsEmail = "maria.souza@example.com",
                itemsPhone = "11999999999",
                whatsapp = "11988888888",
                itemsWhatsapp = "11977777777",
                stockEmail = "estoque@example.com",
                billingEmail = "financeiro@example.com",
                nfEmail = "notas@example.com",
                site = "https://fornecedor-atualizado.com"
            )
        )

        val result = supplierService.updateSupplier(supplier.id!!, dto)

        assertEquals("Maria Souza", result.contact.sellerName)
        assertEquals("maria.souza@example.com", result.contact.itemsEmail)
        verify(contactRepository).save(any())
    }

    @Test
    fun `should update supplier address`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        val updatedAddress = supplier.address.copy(city = "São Paulo", street = "Rua Nova 123")
        whenever(addressService.update(any(), any())).thenReturn(updatedAddress)
        whenever(supplierRepository.save(any())).thenReturn(supplier.copy(address = updatedAddress))

        val dto = UpdateSupplierDTO(
            address = UpdateAddressDTO(
                street = "Rua Nova 123",
                city = "São Paulo",
                cep = "04567000",
                country = "Brasil",
                stateId = 25
            )
        )

        val result = supplierService.updateSupplier(supplier.id!!, dto)

        assertEquals("São Paulo", result.address.city)
        assertEquals("Rua Nova 123", result.address.street)
        verify(addressService).update(any(), any())
    }

    @Test
    fun `should update supplier description`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        val newDescription = Description(id = 2, description = "Distribuidor")
        whenever(descriptionService.findDescriptionById(2)).thenReturn(newDescription)
        whenever(supplierRepository.save(any())).thenReturn(supplier.copy(description = newDescription))

        val dto = UpdateSupplierDTO(descriptionId = 2)

        val result = supplierService.updateSupplier(supplier.id!!, dto)

        assertEquals("Distribuidor", result.description?.description)
        verify(descriptionService).findDescriptionById(2)
    }

    @Test
    fun `should throw NotFoundException when updating missing supplier`() {
        whenever(supplierRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            supplierService.updateSupplier(123, UpdateSupplierDTO(name = "Invalid"))
        }
    }

    @Test
    fun `should delete supplier`() {
        val supplier = buildSupplier()
        whenever(supplierRepository.findById(supplier.id!!)).thenReturn(Optional.of(supplier))

        supplierService.deleteSupplier(supplier.id!!)

        verify(supplierRepository).deleteById(supplier.id!!)
    }

    @Test
    fun `should throw NotFoundException when deleting missing supplier`() {
        whenever(supplierRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            supplierService.deleteSupplier(404)
        }
    }

    private fun buildSupplier(): Supplier {
        val state = BrazilianState(id = 1, stateCode = "MG", stateName = "Minas Gerais")
        val address = Address(
            id = 1,
            street = "Rua A, 123",
            city = "Belo Horizonte",
            state = state,
            cep = "30100-000",
            country = "Brasil"
        )
        val contact = Contact(
            id = 1,
            sellerName = "João Silva",
            itemsEmail = "joao@exemplo.com",
            itemsPhone = "31999999999",
            whatsapp = "31988888888",
            itemsWhatsapp = "31977777777",
            stockEmail = "estoque@exemplo.com",
            billingEmail = "financeiro@exemplo.com",
            nfEmail = "nf@exemplo.com",
            site = "https://exemplo.com"
        )

        return Supplier(
            id = 1,
            name = "Supplier 1",
            supplierOriginalLink = "http://link.com",
            socialName = "Supplier Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = address,
            contact = contact
        )
    }
}
