package org.pecasonline.features.stock.email.sender

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.internet.MimeMessage
import org.pecasonline.common.Constants.CONTACT_EMAIL
import org.pecasonline.common.warnWithoutStacktrace
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
    private val isEmailEnabled: Boolean,

    @Value("\${app.site_url}")
    private val siteUrl: String
) {
    private val EMAIL_NOT_ENABLED_MESSAGE = "Email service is disabled. Notification email will not be sent."
    // private val supplierEmail = "pecas.online.agora@gmail.com" // Para evitar mandar e-mail pra fornecedor que existe mesmo.

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

        sendEmail(supplierEmail, subject, htmlContent)
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

        sendEmail(supplierEmail, subject, htmlContent)
    }

    @Async
    fun sendMagicLink(supplierEmail: String, supplierName: String, token: String) {
        validateIfEmailIsEnabled()

        val subject = "O seu link para acesso ao Peças Online X"
        val magicLink = "${siteUrl}/auth/$token"

        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Você requisitou acesso ao sistema Peças Online X. Use o link abaixo para realizar o login de forma segura:</p>
                <p><a href='$magicLink' target='_blank'>Clique aqui para acessar</a></p>
                <p>Este link é válido por tempo limitado. Caso você não tenha solicitado este acesso, por favor, ignore este e-mail.</p>
                <p>Atenciosamente,<br>Equipe de Suporte</p>
            </body>
            </html>
        """.trimIndent()

        sendEmail(supplierEmail, subject, htmlContent)
    }

    @Async
    fun processAndSendContactForm(name: String, email: String, subject: String, message: String) {
        validateIfEmailIsEnabled()

        val emailSubject = "Nova mensagem do formulário de contato: $subject"

        val htmlContent = """
        <html>
        <body>
            <p>Olá,</p>
            <p>Você recebeu uma nova mensagem através do formulário de contato:</p>
            <p><strong>Nome:</strong> $name</p>
            <p><strong>Email:</strong> $email</p>
            <p><strong>Assunto:</strong> $subject</p>
            <p><strong>Mensagem:</strong></p>
            <p>$message</p>
            <p>Atenciosamente,<br>Equipe do sistema</p>
        </body>
        </html>
    """.trimIndent()

        sendEmail(CONTACT_EMAIL, emailSubject, htmlContent)
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
        if (!isEmailEnabled) warnWithoutStacktrace(EMAIL_NOT_ENABLED_MESSAGE)
    }
}