package org.pecasonline.contact.dto

import jakarta.validation.constraints.Email

data class ContactForm(
    val name: String,
    @field:Email(message = "Use um e-mail válido")
    val email: String,
    val subject: String,
    val message: String
)