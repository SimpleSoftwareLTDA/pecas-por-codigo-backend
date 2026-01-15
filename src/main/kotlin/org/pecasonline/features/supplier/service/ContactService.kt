package org.pecasonline.features.supplier.service

import org.pecasonline.features.supplier.domain.Contact
import org.pecasonline.features.supplier.dto.CreateContactDTO
import org.pecasonline.features.supplier.dto.UpdateContactDTO
import org.pecasonline.features.supplier.repository.ContactRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContactService(
    private val contactRepository: ContactRepository
) : IContactService {

    @Transactional
    override fun save(contact: CreateContactDTO): Contact {
        return contactRepository.save(contact.toContact())
    }

    @Transactional
    override fun update(existingContact: Contact, updatedContact: UpdateContactDTO): Contact {
        // If any phone field is provided, use it for all phone fields
        val phoneValue = updatedContact.itemsPhone 
            ?: updatedContact.whatsapp 
            ?: updatedContact.itemsWhatsapp 
            ?: existingContact.itemsPhone
        
        val contactToPersist = existingContact.copy(
            sellerName = updatedContact.sellerName ?: existingContact.sellerName,
            itemsEmail = updatedContact.itemsEmail ?: existingContact.itemsEmail,
            itemsPhone = phoneValue,
            whatsapp = phoneValue,
            itemsWhatsapp = phoneValue,
            stockEmail = updatedContact.stockEmail ?: existingContact.stockEmail,
            billingEmail = updatedContact.billingEmail ?: existingContact.billingEmail,
            nfEmail = updatedContact.nfEmail ?: existingContact.nfEmail,
            site = updatedContact.site ?: existingContact.site
        )

        return contactRepository.save(contactToPersist)
    }
}
