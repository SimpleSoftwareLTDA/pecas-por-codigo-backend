package org.pecasonline.features.plan.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.plan.PlanRepository
import org.pecasonline.features.plan.PlanService
import java.util.Optional

class PlanServiceTest {

    private val planRepository: PlanRepository = mockk()
    private val planService = PlanService(planRepository)

    @Test
    fun `should return all available plans`() {
        val plans = listOf(
            Plan(name = "Basic Plan", priceInCents = 5000, stock = true, quote = false, smallBanner = true, bigBanner = false),
            Plan(name = "Premium Plan", priceInCents = 15000, stock = false, quote = true, smallBanner = false, bigBanner = true)
        )
        
        every { planRepository.findAll() } returns plans

        val result = planService.getAvailablePlans()

        assertEquals(2, result.size)
        assertEquals("Basic Plan", result[0].name)
        assertEquals("Premium Plan", result[1].name)

        verify { planRepository.findAll() }
    }

    @Test
    fun `should return plan by id`() {
        val plan = Plan(name = "Basic Plan", priceInCents = 5000, stock = true, quote = false, smallBanner = true, bigBanner = false)
        
        every { planRepository.findById(1) } returns Optional.of(plan)

        val result = planService.getPlanById(1)

        assertNotNull(result)
        assertEquals("Basic Plan", result.name)

        verify { planRepository.findById(1) }
    }

    @Test
    fun `should throw NotFoundException when plan id is not found`() {
        every { planRepository.findById(99) } returns Optional.empty()

        val exception = assertThrows<NotFoundException> {
            planService.getPlanById(99)
        }

        assertEquals("Plano de assinatura não encontrado.", exception.message)
        verify { planRepository.findById(99) }
    }
}
