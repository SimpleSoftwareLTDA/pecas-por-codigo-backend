package org.pecasonline.features.description

interface IDescriptionService {
    fun getAvailableDescriptions(): List<Description>
    fun findDescriptionById(id: Int): Description
}