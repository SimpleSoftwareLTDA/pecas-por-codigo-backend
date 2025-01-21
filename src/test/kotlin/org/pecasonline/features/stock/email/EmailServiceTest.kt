package org.pecasonline.features.stock.email

import io.mockk.*
import jakarta.mail.Multipart
import jakarta.mail.Session
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper

class EmailServiceTest {

    private lateinit var emailService: EmailSenderService
    private val emailSender: JavaMailSender = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        emailService = EmailSenderService(emailSender, isEmailEnabled = true, siteUrl = "blah")
    }


    @Test
    fun `should send stock processing start notification when email is enabled`() {
        val supplierEmail = "test@example.com"
        val supplierName = "Supplier Name"
        val fileName = "inventory.csv"

        val session = Session.getDefaultInstance(java.util.Properties())
        val mimeMessage = MimeMessage(session)
        every { emailSender.createMimeMessage() } returns mimeMessage

        emailService.sendStockProcessingStartNotification(supplierEmail, supplierName, fileName)

        val recipient = mimeMessage.getRecipients(MimeMessage.RecipientType.TO)?.first().toString()
        assertEquals(supplierEmail, recipient, "Expected recipient email to be $supplierEmail, but was $recipient")

        val subject = mimeMessage.subject
        assertEquals("Início do Processamento de Estoque", subject, "Expected subject to be 'Início do Processamento de Estoque', but was $subject")

        val bodyContent = extractTextFromMimeMessage(mimeMessage)

        assertTrue(bodyContent.contains("Olá $supplierName"), "Expected content to contain 'Olá $supplierName', but was: $bodyContent")
        assertTrue(bodyContent.contains("arquivo: <strong>$fileName</strong>"), "Expected content to contain 'arquivo: <strong>$fileName</strong>', but was: $bodyContent")
    }

    @Test
    fun `should not send email and log message when email is disabled`() {
        emailService = EmailSenderService(emailSender, isEmailEnabled = false, siteUrl = "blah")
        emailService.sendStockProcessingStartNotification("test@example.com", "Supplier Name", "inventory.csv")

        verify(exactly = 0) { emailSender.send(any<MimeMessage>()) }
    }

    @Test
    fun `should send stock processing completion notification`() {
        val supplierEmail = "test@example.com"
        val supplierName = "Supplier Name"
        val fileName = "inventory.csv"
        val updatedItemCount = 10

        val mimeMessage = mockk<MimeMessage>(relaxed = true)
        every { emailSender.createMimeMessage() } returns mimeMessage

        val toSlot = slot<String>()
        val subjectSlot = slot<String>()
        val textSlot = slot<String>()

        mockkConstructor(MimeMessageHelper::class)
        every { anyConstructed<MimeMessageHelper>().setTo(capture(toSlot)) } just Runs
        every { anyConstructed<MimeMessageHelper>().setSubject(capture(subjectSlot)) } just Runs
        every { anyConstructed<MimeMessageHelper>().setText(capture(textSlot), true) } just Runs

        emailService.sendStockProcessingCompletionNotification(supplierEmail, supplierName, fileName, updatedItemCount)

        assertEquals(supplierEmail, toSlot.captured)
        assertEquals("Processamento de Estoque Concluído", subjectSlot.captured)
        assert(textSlot.captured.contains("Olá $supplierName"))
        assert(textSlot.captured.contains("arquivo <strong>$fileName</strong>"))
        assert(textSlot.captured.contains("<strong>$updatedItemCount</strong> itens foi atualizado"))
    }

    @Test
    fun `should send stock processing error notification`() {
        val supplierEmail = "test@example.com"
        val supplierName = "Supplier Name"
        val fileName = "inventory.csv"
        val errorMessage = "Some error occurred"

        val session = Session.getDefaultInstance(java.util.Properties())
        val mimeMessage = MimeMessage(session)
        every { emailSender.createMimeMessage() } returns mimeMessage

        emailService.sendStockProcessingErrorNotification(supplierEmail, supplierName, fileName, errorMessage)

        val recipient = mimeMessage.getRecipients(MimeMessage.RecipientType.TO)?.first().toString()
        assertEquals(supplierEmail, recipient, "Expected recipient email to be $supplierEmail, but was $recipient")

        val subject = mimeMessage.subject
        assertEquals("Erro no Processamento de Estoque", subject, "Expected subject to be 'Erro no Processamento de Estoque', but was $subject")

        val bodyContent = extractTextFromMimeMessage(mimeMessage)

        assertTrue(bodyContent.contains("Olá $supplierName"), "Expected content to contain 'Olá $supplierName', but was: $bodyContent")
        assertTrue(bodyContent.contains("arquivo: <strong>$fileName</strong>"), "Expected content to contain 'arquivo: <strong>$fileName</strong>', but was: $bodyContent")
        assertTrue(bodyContent.contains("Erro: <strong>$errorMessage</strong>"), "Expected content to contain 'Erro: <strong>$errorMessage</strong>', but was: $bodyContent")
    }

    companion object {
        private fun extractTextFromMimeMessage(mimeMessage: MimeMessage): String {
            val content = mimeMessage.content
            return when (content) {
                is String -> content
                is MimeMultipart -> extractTextFromMimeMultipart(content)
                else -> throw AssertionError("Unsupported content type: ${content::class.simpleName}")
            }
        }

        private fun extractTextFromMimeMultipart(multipart: Multipart): String {
            for (i in 0 until multipart.count) {
                val bodyPart = multipart.getBodyPart(i)
                val contentType = bodyPart.contentType.lowercase()

                if (bodyPart.content is String && (contentType.contains("text/html") || contentType.contains("text/plain"))) {
                    return bodyPart.content.toString()
                } else if (bodyPart.content is Multipart) {
                    return extractTextFromMimeMultipart(bodyPart.content as Multipart)
                }
            }
            throw AssertionError("No text/html or text/plain part found")
        }
    }
}
