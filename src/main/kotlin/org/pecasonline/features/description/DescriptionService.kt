package org.pecasonline.features.description

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.stereotype.Service

@Service
class DescriptionService(
    private val descriptionRepository: DescriptionRepository,
) : IDescriptionService {

    override fun getAvailableDescriptions(): List<Description> = descriptionRepository.findAll()

    override fun findDescriptionById(id: Int) = descriptionRepository.findById(id)
        .orElseThrow { NotFoundException("Descrição não encontrada.") }
}