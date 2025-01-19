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
import org.mockito.kotlin.whenever
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.service.IAddressService
import org.pecasonline.features.brand.IBrandService
import org.pecasonline.features.description.IDescriptionService
import org.pecasonline.features.subscription.ISubscriptionService
import org.pecasonline.features.supplier.domain.Supplier
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

    @InjectMocks
    private lateinit var supplierService: SupplierService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should find suppliers successfully`() {
        val supplier = Supplier(
            id = 1,
            name = "Supplier 1",
            supplierOriginalLink = "http://link.com",
            socialName = "Supplier Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = mock(),
            contact = mock()
        )
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
        val supplier = Supplier(
            id = 1,
            name = "Supplier 1",
            supplierOriginalLink = "http://link.com",
            socialName = "Supplier Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = mock(),
            contact = mock()
        )

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
}
