package org.pecasonline.contact

import jakarta.validation.Valid
import org.pecasonline.contact.dto.ContactForm
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class ContactController(
    val emailSenderService: EmailSenderService
) {

    @PostMapping("/contact-form")
    fun processAndSendContactForm(@Valid @RequestBody contactForm: ContactForm) {
        val (name, email, subject, message) = contactForm
        emailSenderService.processAndSendContactForm(name, email, subject, message)
    }
}

