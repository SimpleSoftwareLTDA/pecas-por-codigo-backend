package org.pecasonline.features.supplier.repository

import org.pecasonline.features.supplier.domain.Contact
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : JpaRepository<Contact, Int> {
    fun existsByItemsEmail(itemsEmail: String): Boolean
}