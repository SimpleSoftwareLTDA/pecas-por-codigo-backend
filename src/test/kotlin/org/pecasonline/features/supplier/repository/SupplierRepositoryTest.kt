package org.pecasonline.features.supplier.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.AddressRepository
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.brand.BrandRepository
import org.pecasonline.features.description.Description
import org.pecasonline.features.description.DescriptionRepository
import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.domain.Supplier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@DataJpaTest
@Transactional
class SupplierRepositoryTest {

    @Autowired
    private lateinit var supplierRepository: SupplierRepository

    @Autowired
    private lateinit var brandRepository: BrandRepository

    @Autowired
    private lateinit var descriptionRepository: DescriptionRepository

    @Autowired
    private lateinit var addressRepository: AddressRepository

    @Autowired
    private lateinit var stateRepository: BrazilianStatesRepository

    @Test
    fun `should save and retrieve supplier successfully`() {
        val state = BrazilianState(
            stateName = "São Paulo",
            stateCode = "SP"
        )
        val savedState = stateRepository.save(state)

        val address = Address(
            street = "123 Main St",
            city = "Sample City",
            state = savedState,
            country = "Brazil",
            cep = "12345-678"
        )
        val savedAddress = addressRepository.save(address)

        val brand = Brand(brandName = "Brand X")
        val savedBrand = brandRepository.save(brand)

        val description = Description(description = "Electronics Supplier")
        val savedDescription = descriptionRepository.save(description)

        val contact = Contact(
            sellerName = "John Doe",
            itemsEmail = "items@example.com",
            itemsPhone = "1234567890"
        )

        val supplier = Supplier(
            name = "Test Supplier",
            supplierOriginalLink = "http://supplierlink.com",
            socialName = "Test Social Name",
            cnpj = "15826705000130",
            stateSubscription = "123456789",
            address = savedAddress,
            contact = contact,
            description = savedDescription,
            brand = savedBrand
        )

        val savedSupplier = supplierRepository.save(supplier)

        assertNotNull(savedSupplier.id)

        val retrievedSupplier = supplierRepository.findById(savedSupplier.id!!).orElse(null)
        assertNotNull(retrievedSupplier)
        assertEquals(supplier.name, retrievedSupplier?.name)
        assertEquals(supplier.cnpj, retrievedSupplier?.cnpj)
        assertEquals(savedAddress, retrievedSupplier?.address)
        assertEquals(contact, retrievedSupplier?.contact)
    }
}
