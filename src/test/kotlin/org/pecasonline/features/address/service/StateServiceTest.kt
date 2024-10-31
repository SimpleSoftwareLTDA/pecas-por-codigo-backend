package org.pecasonline.features.address.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import java.util.*

class StateServiceTest {

    private val stateRepository: BrazilianStatesRepository = mock()
    private val stateService = StateService(stateRepository)

    @Test
    fun `should return all available states`() {
        val states = listOf(
            BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP"),
            BrazilianState(id = 2, stateName = "Rio de Janeiro", stateCode = "RJ")
        )
        
        whenever(stateRepository.findAll()).thenReturn(states)

        val result = stateService.getAvailableStates()

        assertEquals(2, result.size)
        assertEquals("São Paulo", result[0].stateName)
        assertEquals("Rio de Janeiro", result[1].stateName)

        verify(stateRepository).findAll()
    }

    @Test
    fun `should return a state by id`() {
        val state = BrazilianState(id = 1, stateName = "São Paulo", stateCode = "SP")
        
        whenever(stateRepository.findById(1)).thenReturn(Optional.of(state))

        val result = stateService.findStateById(1)

        assertNotNull(result)
        assertEquals("São Paulo", result?.stateName)
        assertEquals("SP", result?.stateCode)

        verify(stateRepository).findById(1)
    }

    @Test
    fun `should return null when state id is not found`() {
        whenever(stateRepository.findById(99)).thenReturn(Optional.empty())

        val result = stateService.findStateById(99)

        assertNull(result)
        verify(stateRepository).findById(99)
    }
}
