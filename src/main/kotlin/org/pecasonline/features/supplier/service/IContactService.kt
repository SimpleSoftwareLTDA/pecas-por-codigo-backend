package org.pecasonline.features.supplier.service

import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.dto.CreateContactDTO
import org.pecasonline.features.supplier.dto.UpdateContactDTO

interface IContactService {
    fun save(contact: CreateContactDTO): Contact
    fun update(existingContact: Contact, updatedContact: UpdateContactDTO): Contact
}
