package org.pecasonline.features.plan.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.plan.PlanRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@DataJpaTest
@ExtendWith(SpringExtension::class)
class PlanRepositoryTest @Autowired constructor(
    private val planRepository: PlanRepository
) {

    @BeforeEach
    fun setUp() {
        planRepository.deleteAll()
    }

    @Test
    fun `should save and retrieve a plan`() {
        val plan = Plan(
            name = "Basic Plan",
            priceInCents = 5000,
            stock = true,
            quote = false,
            smallBanner = true,
            bigBanner = false
        )
        val savedPlan = planRepository.save(plan)
        val retrievedPlan = planRepository.findById(savedPlan.id!!)

        assertTrue(retrievedPlan.isPresent)
        assertEquals("Basic Plan", retrievedPlan.get().name)
        assertEquals(5000, retrievedPlan.get().priceInCents)
        assertTrue(retrievedPlan.get().stock)
        assertFalse(retrievedPlan.get().quote)
        assertTrue(retrievedPlan.get().smallBanner)
        assertFalse(retrievedPlan.get().bigBanner)
    }

    @Test
    fun `should delete a plan`() {
        val plan = Plan(
            name = "Pro Plan",
            priceInCents = 10000,
            stock = true,
            quote = true,
            smallBanner = true,
            bigBanner = true
        )
        val savedPlan = planRepository.save(plan)
        planRepository.deleteById(savedPlan.id!!)

        val retrievedPlan = planRepository.findById(savedPlan.id!!)
        assertFalse(retrievedPlan.isPresent)
    }

    @Test
    fun `should find all plans`() {
        val plan1 = Plan(
            name = "Basic Plan",
            priceInCents = 5000,
            stock = true,
            quote = false,
            smallBanner = true,
            bigBanner = false
        )
        val plan2 = Plan(
            name = "Premium Plan",
            priceInCents = 15000,
            stock = false,
            quote = true,
            smallBanner = false,
            bigBanner = true
        )
        planRepository.saveAll(listOf(plan1, plan2))

        val plans = planRepository.findAll()
        assertEquals(2, plans.size)
        assertTrue(plans.any { it.name == "Basic Plan" })
        assertTrue(plans.any { it.name == "Premium Plan" })
    }
}
