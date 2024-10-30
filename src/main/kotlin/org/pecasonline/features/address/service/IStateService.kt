package org.pecasonline.features.address.service

import org.pecasonline.features.address.domain.BrazilianState

interface IStateService {
    fun getAvailableStates(): List<BrazilianState>
    fun findStateById(id: Int): BrazilianState?
}