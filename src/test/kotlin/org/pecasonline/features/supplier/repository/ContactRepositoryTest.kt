package org.pecasonline.features.supplier.repository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.supplier.domain.Contact
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private lateinit var contactRepository: ContactRepository

    @Test
    fun `should save and retrieve contact successfully`() {
        val contact = Contact(
            sellerName = "John Doe",
            itemsEmail = "items@example.com",
            itemsPhone = "1234567890",
            whatsapp = "0987654321",
            itemsWhatsapp = "1122334455",
            stockEmail = "stock@example.com",
            billingEmail = "billing@example.com",
            nfEmail = "nfe@example.com",
            site = "http://example.com"
        )

        val savedContact = contactRepository.save(contact)

        assertNotNull(savedContact.id)

        val retrievedContact = contactRepository.findById(savedContact.id!!).orElse(null)
        assertNotNull(retrievedContact)
        assertEquals(contact.sellerName, retrievedContact?.sellerName)
        assertEquals(contact.itemsEmail, retrievedContact?.itemsEmail)
        assertEquals(contact.itemsPhone, retrievedContact?.itemsPhone)
        assertEquals(contact.whatsapp, retrievedContact?.whatsapp)
        assertEquals(contact.itemsWhatsapp, retrievedContact?.itemsWhatsapp)
        assertEquals(contact.stockEmail, retrievedContact?.stockEmail)
        assertEquals(contact.billingEmail, retrievedContact?.billingEmail)
        assertEquals(contact.nfEmail, retrievedContact?.nfEmail)
        assertEquals(contact.site, retrievedContact?.site)
    }
}
