package org.pecasonline.features.email

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value

@Service
class EmailService(
    private val emailSender: JavaMailSender,
    @Value("\${spring.mail.enabled}") private val emailEnabled: Boolean
) {
    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    private val EMAIL_NOT_ENABLED_MESSAGE = "Email service is disabled. Notification email will not be sent."

    fun sendStockProcessingStartNotification(supplierEmail: String, supplierName: String, fileName: String) {
        if(!emailEnabled) {
            logger.warn(EMAIL_NOT_ENABLED_MESSAGE)
            return
        }
        val subject = "Início do Processamento de Estoque"
        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Estamos iniciando o processamento do estoque para o arquivo: <strong>$fileName</strong>.</p>
                <p>Por favor, aguarde até que o processamento seja concluído. Qualquer problema ou dúvida, entre em contato conosco.</p>
                <p>Atenciosamente,<br>Equipe de Estoque</p>
            </body>
            </html>
        """.trimIndent()
        sendEmail(supplierEmail, subject, htmlContent)
    }

    fun sendStockProcessingCompletionNotification(supplierEmail: String, supplierName: String, fileName: String, updatedItemCount: Int) {
        if (!emailEnabled) {
            logger.warn(EMAIL_NOT_ENABLED_MESSAGE)
            return
        }
        val subject = "Processamento de Estoque Concluído"
        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Informamos que o processamento do estoque para o arquivo <strong>$fileName</strong> foi concluído com sucesso.</p>
                <p>Um total de <strong>$updatedItemCount</strong> itens foi atualizado.</p>
                <p>Se houver alguma dúvida ou problema, não hesite em nos contatar.</p>
                <p>Atenciosamente,<br>Equipe de Estoque</p>
            </body>
            </html>
        """.trimIndent()
        sendEmail(supplierEmail, subject, htmlContent)
    }

    fun sendStockProcessingErrorNotification(supplierEmail: String, supplierName: String, fileName: String, errorMessage: String) {
        if (!emailEnabled) {
            logger.warn(EMAIL_NOT_ENABLED_MESSAGE)
            return
        }
        val subject = "Erro no Processamento de Estoque"
        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Infelizmente, ocorreu um problema ao processar o estoque para o arquivo: <strong>$fileName</strong>.</p>
                <p>Erro: <strong>$errorMessage</strong></p>
                <p>Recomendamos que você revise o arquivo e tente novamente. Caso o problema persista, por favor, entre em contato com nosso suporte para assistência.</p>
                <p>Atenciosamente,<br>Equipe de Estoque</p>
            </body>
            </html>
        """.trimIndent()
        sendEmail(supplierEmail, subject, htmlContent)
    }

    private fun sendEmail(supplierEmail: String, subject: String, htmlContent: String) {
        try {
            val message: MimeMessage = emailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)
            helper.setTo(supplierEmail)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            emailSender.send(message)
            logger.info("Sent email with subject '{}' to: {}", subject, supplierEmail)
        } catch (ex: Exception) {
            logger.error("Failed to send email to: {}", supplierEmail, ex)
        }
    }
}
