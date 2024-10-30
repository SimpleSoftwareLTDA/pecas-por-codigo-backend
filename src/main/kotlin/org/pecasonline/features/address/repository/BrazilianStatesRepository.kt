package org.pecasonline.features.address.repository

import org.pecasonline.features.address.domain.BrazilianState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BrazilianStatesRepository : JpaRepository<BrazilianState, Int> {
    fun findByStateCode(stateCode: String): BrazilianState?
}