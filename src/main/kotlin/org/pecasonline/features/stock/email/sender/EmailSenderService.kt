package org.pecasonline.features.stock.email.sender

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class EmailSenderService(
    private val emailSender: JavaMailSender,

    @Value("\${spring.mail.enabled}")
    private val isEmailEnabled: Boolean
) {
    private val EMAIL_NOT_ENABLED_MESSAGE = "Email service is disabled. Notification email will not be sent."
    private val supplierEmail = "pecas.online.agora@gmail.com" // Para evitar mandar e-mail pra fornecedor que existe mesmo.

    @Async
    fun sendStockProcessingStartNotification(supplierEmail: String, supplierName: String, fileName: String) {
        validateIfEmailIsEnabled()

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

    @Async
    fun sendStockProcessingCompletionNotification(
        supplierEmail: String,
        supplierName: String,
        fileName: String,
        updatedItemCount: Int
    ) {
        validateIfEmailIsEnabled()

        val subject = "Processamento de Estoque Concluído"
        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Informamos que o processamento do estoque para o arquivo <strong>$fileName</strong> foi concluído com sucesso.</p>
                <p>Total de itens atualizados: <strong>$updatedItemCount</strong>.</p>
                <p>Se houver alguma dúvida ou problema, não hesite em nos contatar.</p>
                <p>Atenciosamente,<br>Equipe de Estoque</p>
            </body>
            </html>
        """.trimIndent()

        sendEmail(this.supplierEmail, subject, htmlContent)
    }

    @Async
    fun sendStockProcessingErrorNotification(
        supplierEmail: String,
        supplierName: String = "",
        fileName: String,
        errorMessage: String
    ) {
        validateIfEmailIsEnabled()

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

        sendEmail(this.supplierEmail, subject, htmlContent)
    }

    private fun sendEmail(supplierEmail: String, subject: String, htmlContent: String) {
        runCatching {
            val message: MimeMessage = emailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setTo(supplierEmail)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            emailSender.send(message)

            logger.info { "Sent email with subject '$subject' to: $supplierEmail" }
        }.onFailure { ex ->
            logger.error { "${"Failed to send email to: {}"} $supplierEmail $ex" }
        }
    }

    private fun validateIfEmailIsEnabled() {
        if (!isEmailEnabled) {
            logger.warn { EMAIL_NOT_ENABLED_MESSAGE }
            return
        }
    }

}