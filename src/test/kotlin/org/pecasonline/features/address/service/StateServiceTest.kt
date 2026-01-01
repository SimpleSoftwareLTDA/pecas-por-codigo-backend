package org.pecasonline.features.address.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import java.util.*

class StateServiceTest {

    private val stateRepository: BrazilianStatesRepository = mockk()
    private val stateService = StateService(stateRepository)

    @Test
    fun `should return all available states`() {
        val states = listOf(
            BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP"),
            BrazilianState(id = 2, stateName = "Rio de Janeiro", stateCode = "RJ")
        )
        
        every { stateRepository.findAll() } returns states

        val result = stateService.getAvailableStates()

        assertEquals(2, result.size)
        assertEquals("São Paulo", result[0].stateName)
        assertEquals("Rio de Janeiro", result[1].stateName)

        verify { stateRepository.findAll() }
    }

    @Test
    fun `should return a state by id`() {
        val state = BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP")
        
        every { stateRepository.findById(1) } returns Optional.of(state)

        val result = stateService.findStateById(1)

        assertNotNull(result)
        assertEquals("São Paulo", result?.stateName)
        assertEquals("SP", result?.stateCode)

        verify { stateRepository.findById(1) }
    }

    @Test
    fun `should return null when state id is not found`() {
        every { stateRepository.findById(99) } returns Optional.empty()

        val result = stateService.findStateById(99)

        assertNull(result)
        verify { stateRepository.findById(99) }
    }
}
