package org.pecasonline.features.plan

interface IPlanService {
    fun getAvailablePlans(): List<Plan>
    fun getPlanById(id: Int): Plan
}