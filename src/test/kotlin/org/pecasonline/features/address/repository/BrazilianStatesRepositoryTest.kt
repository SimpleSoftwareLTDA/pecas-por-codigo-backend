package org.pecasonline.features.address.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.address.domain.BrazilianState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class BrazilianStatesRepositoryTest @Autowired constructor(
    private val brazilianStatesRepository: BrazilianStatesRepository
) {

    @Test
    fun `should save and retrieve a state by state code`() {
        val state = BrazilianState(stateName = "São Paulo", stateCode = "SP")
        brazilianStatesRepository.save(state)

        val retrievedState = brazilianStatesRepository.findByStateCode("SP")

        assertNotNull(retrievedState)
        assertEquals("São Paulo", retrievedState?.stateName)
        assertEquals("SP", retrievedState?.stateCode)
    }

    @Test
    fun `should return null for non-existent state code`() {
        val retrievedState = brazilianStatesRepository.findByStateCode("XX")

        assertNull(retrievedState)
    }

    @Test
    fun `should find all states`() {
        val state1 = BrazilianState(stateName = "Rio de Janeiro", stateCode = "RJ")
        val state2 = BrazilianState(stateName = "Minas Gerais", stateCode = "MG")
        brazilianStatesRepository.saveAll(listOf(state1, state2))

        val states = brazilianStatesRepository.findAll()

        assertEquals(2, states.size)
        assertTrue(states.any { it.stateCode == "RJ" && it.stateName == "Rio de Janeiro" })
        assertTrue(states.any { it.stateCode == "MG" && it.stateName == "Minas Gerais" })
    }
}
