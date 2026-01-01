package org.pecasonline.features.address.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.dto.CreateAddressDTO
import org.pecasonline.features.address.repository.AddressRepository

class AddressServiceTest {

    private val addressRepository: AddressRepository = mockk()
    private val stateService: IStateService = mockk()
    private val addressService = AddressService(addressRepository, stateService)

    @Test
    fun `should save address when state is valid`() {
        val state = BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP")
        val createAddressDTO = CreateAddressDTO(
            street = "123 Test St",
            city = "Testville",
            cep = "12345-678",
            country = "Brasil",
            stateId = state.id
        )
        val expectedAddress = Address(
            id = 1,
            street = createAddressDTO.street!!,
            city = createAddressDTO.city!!,
            cep = createAddressDTO.cep!!,
            country = createAddressDTO.country,
            state = state
        )

        every { stateService.findStateById(createAddressDTO.stateId!!) } returns state
        every { addressRepository.save(any()) } returns expectedAddress

        val savedAddress = addressService.save(createAddressDTO)

        assertNotNull(savedAddress)
        assertEquals(expectedAddress.street, savedAddress.street)
        assertEquals(expectedAddress.city, savedAddress.city)
        assertEquals(expectedAddress.cep, savedAddress.cep)
        assertEquals(expectedAddress.country, savedAddress.country)
        assertEquals(expectedAddress.state, savedAddress.state)

        verify { stateService.findStateById(createAddressDTO.stateId!!) }
        verify { addressRepository.save(any()) }
    }

    @Test
    fun `should throw NotFoundException when state is invalid`() {
        val createAddressDTO = CreateAddressDTO(
            street = "123 Test St",
            city = "Testville",
            cep = "12345-678",
            country = "Brasil",
            stateId = 99
        )

        every { stateService.findStateById(createAddressDTO.stateId!!) } returns null

        val exception = assertThrows<NotFoundException> {
            addressService.save(createAddressDTO)
        }

        assertEquals("Invalid state id: 99. No state found in the database", exception.message)

        verify { stateService.findStateById(createAddressDTO.stateId!!) }
        verify(exactly = 0) { addressRepository.save(any()) }
    }
}
