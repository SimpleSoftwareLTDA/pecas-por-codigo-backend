package org.pecasonline.features.address.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class AddressRepositoryTest @Autowired constructor(
    private val addressRepository: AddressRepository,
    private val stateRepository: BrazilianStatesRepository // Assuming you have a repository for BrazilianState
) {

    @Test
    fun `should save and retrieve an address`() {
        val state = BrazilianState(stateName = "São Paulo", stateCode = "SP")
        val savedState = stateRepository.save(state)

        val address = Address(
            street = "123 Test St",
            city = "Testville",
            state = savedState,
            cep = "12345-678",
            country = "Brasil"
        )

        val savedAddress = addressRepository.save(address)
        val retrievedAddress = addressRepository.findById(savedAddress.id!!)

        assertTrue(retrievedAddress.isPresent)
        assertEquals("123 Test St", retrievedAddress.get().street)
        assertEquals("Testville", retrievedAddress.get().city)
        assertEquals("SP", retrievedAddress.get().state.stateCode)
        assertEquals("12345-678", retrievedAddress.get().cep)
        assertEquals("Brasil", retrievedAddress.get().country)
    }

    @Test
    fun `should delete an address`() {
        val state = BrazilianState(stateName = "Rio de Janeiro", stateCode = "RJ")
        val savedState = stateRepository.save(state)

        val address = Address(
            street = "456 Test Ave",
            city = "Sampletown",
            state = savedState,
            cep = "98765-432",
            country = "Brasil"
        )
        val savedAddress = addressRepository.save(address)

        addressRepository.deleteById(savedAddress.id!!)

        val retrievedAddress = addressRepository.findById(savedAddress.id!!)
        assertFalse(retrievedAddress.isPresent)
    }

    @Test
    fun `should find all addresses`() {
        val state = BrazilianState(stateName = "Minas Gerais", stateCode = "MG")
        val savedState = stateRepository.save(state)

        val address1 = Address(
            street = "789 Test Blvd",
            city = "Example City",
            state = savedState,
            cep = "11223-445",
            country = "Brasil"
        )
        val address2 = Address(
            street = "101 Test Ln",
            city = "Demo Town",
            state = savedState,
            cep = "55667-889",
            country = "Brasil"
        )
        addressRepository.saveAll(listOf(address1, address2))

        val addresses = addressRepository.findAll()
        assertEquals(2, addresses.size)
    }
}
