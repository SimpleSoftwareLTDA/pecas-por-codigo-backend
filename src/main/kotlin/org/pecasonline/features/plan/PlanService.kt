package org.pecasonline.features.plan

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class PlanService(
    private val planRepository: PlanRepository,
) : IPlanService {

    override fun getAvailablePlans(): List<Plan> = planRepository.findAll()

    override fun getPlanById(id: Int): Plan = planRepository.findById(id).orElseThrow { NotFoundException("Plano de assinatura não encontrado.") }
}