package org.pecasonline.features.plan.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.plan.PlanRepository
import org.pecasonline.features.plan.PlanService

class PlanServiceTest {

    private val planRepository: PlanRepository = mock()
    private val planService = PlanService(planRepository)

    @Test
    fun `should return all available plans`() {
        val plans = listOf(
            Plan(name = "Basic Plan", priceInCents = 5000, stock = true, quote = false, smallBanner = true, bigBanner = false),
            Plan(name = "Premium Plan", priceInCents = 15000, stock = false, quote = true, smallBanner = false, bigBanner = true)
        )
        
        whenever(planRepository.findAll()).thenReturn(plans)

        val result = planService.getAvailablePlans()

        assertEquals(2, result.size)
        assertEquals("Basic Plan", result[0].name)
        assertEquals("Premium Plan", result[1].name)

        verify(planRepository).findAll()
    }

    @Test
    fun `should return plan by id`() {
        val plan = Plan(name = "Basic Plan", priceInCents = 5000, stock = true, quote = false, smallBanner = true, bigBanner = false)
        
        whenever(planRepository.findById(1)).thenReturn(java.util.Optional.of(plan))

        val result = planService.getPlanById(1)

        assertNotNull(result)
        assertEquals("Basic Plan", result.name)

        verify(planRepository).findById(1)
    }

    @Test
    fun `should throw NotFoundException when plan id is not found`() {
        whenever(planRepository.findById(99)).thenReturn(java.util.Optional.empty())

        val exception = assertThrows<NotFoundException> {
            planService.getPlanById(99)
        }

        assertEquals("Plano de assinatura não encontrado.", exception.message)
        verify(planRepository).findById(99)
    }
}
