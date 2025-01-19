package org.pecasonline.features.address.service

import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.address.repository.BrazilianStatesRepository
import org.springframework.stereotype.Service

@Service
class StateService(
    private val stateRepository: BrazilianStatesRepository,
) : IStateService {
    override fun getAvailableStates(): List<BrazilianState> = stateRepository.findAll()
    override fun findStateById(id: Int): BrazilianState? = stateRepository.findById(id).orElse(null)
}