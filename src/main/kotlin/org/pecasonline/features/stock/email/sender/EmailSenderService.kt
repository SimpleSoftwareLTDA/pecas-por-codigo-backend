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
    private val siteUrl: String,

    @Value("\${app.mail.redirect-to:}")
    private val redirectTo: String,

    @Value("\${spring.mail.from-email}")
    private val fromEmail: String
) {
    init {
        logger.info { "EmailSenderService initialized. Email enabled: $isEmailEnabled, Redirect to: ${redirectTo.ifBlank { "NONE" }}, From: $fromEmail" }
    }

    private val EMAIL_NOT_ENABLED_MESSAGE = "Email service is disabled. Notification email will not be sent."

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
        updatedItemCount: Int,
        attachment: java.io.File? = null
    ) {
        validateIfEmailIsEnabled()

        val subject = "Processamento de Estoque Concluído"
        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Informamos que o processamento do estoque para o arquivo <strong>$fileName</strong> foi concluído.</p>
                <p>Total de itens atualizados com sucesso: <strong>$updatedItemCount</strong>.</p>
                ${if (attachment != null) """
                    <div style="background-color: #fff3cd; border: 1px solid #ffeeba; padding: 15px; margin: 15px 0;">
                        <p><strong>Atenção:</strong> Foram encontradas linhas com erro no arquivo original. Enviamos anexo um arquivo contendo apenas as linhas que não puderam ser processadas para sua correção.</p>
                        <p>Causas comuns de rejeição:</p>
                        <ul>
                            <li><strong>Formato Inválido:</strong> Linhas incompletas ou com caracteres inesperados.</li>
                            <li><strong>Colunas Incorretas:</strong> O arquivo deve conter exatamente 4 colunas (Código, Quantidade, Preço, Descrição).</li>
                            <li><strong>Notação Científica:</strong> Códigos corrompidos pelo Excel (ex: 7,90E+12). Verifique se a coluna de código no seu sistema está formatada como Texto antes de exportar.</li>
                        </ul>
                    </div>
                """.trimIndent() else ""}
                <p>Se houver alguma dúvida ou problema, não hesite em nos contatar.</p>
                <p>Atenciosamente,<br>Equipe de Estoque</p>
            </body>
            </html>
        """.trimIndent()

        val attachmentName = attachment?.let { "itens_nao_processados_$fileName" }
        sendEmail(supplierEmail, subject, htmlContent, attachment, attachmentName)
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

        val subject = "O seu link para acesso ao Peças Por Código"
        val magicLink = "${siteUrl}/auth/$token"

        val htmlContent = """
            <html>
            <body>
                <p>Olá $supplierName,</p>
                <p>Você requisitou acesso ao sistema Peças Por Código. Use o link abaixo para realizar o login de forma segura:</p>
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

    @Async
    fun sendTestEmail(recipient: String) {
        validateIfEmailIsEnabled()
        val subject = "Teste de Serviço de E-mail - Peças Por Código"
        val htmlContent = """
            <html>
            <body>
                <p>Olá,</p>
                <p>Este é um e-mail de teste disparado manualmente para verificar a saúde do serviço de e-mail em produção.</p>
                <p>Data/Hora: ${java.time.LocalDateTime.now()}</p>
                <p>Se você recebeu este e-mail, a integração com o SMTP está funcionando corretamente.</p>
                <p>Atenciosamente,<br>Lighthead (AI Assistant)</p>
            </body>
            </html>
        """.trimIndent()

        sendEmail(recipient, subject, htmlContent)
    }

    private fun sendEmail(
        supplierEmail: String,
        subject: String,
        htmlContent: String,
        attachment: java.io.File? = null,
        attachmentName: String? = null
    ) {
        runCatching {
            val isRedirected = redirectTo.isNotBlank()
            val finalRecipient = if (isRedirected) {
                logger.info { "MAIL REDIRECT ACTIVE: Redirecting email from $supplierEmail to $redirectTo" }
                redirectTo
            } else {
                supplierEmail
            }

            val message: MimeMessage = emailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setFrom(fromEmail)
            helper.setTo(finalRecipient)
            helper.setSubject(subject)
            helper.setText(htmlContent, true)
            
            attachment?.let {
                val finalName = attachmentName ?: "linhas_com_erro_${it.name}"
                helper.addAttachment(finalName, it)
            }
            
            emailSender.send(message)

            val logSuffix = if (isRedirected) " (ORIGINAL RECIPIENT: $supplierEmail)" else ""
            logger.info { "Success: Sent email '$subject' to: $finalRecipient$logSuffix" }
        }.onFailure { ex ->
            logger.error { "Failed to send email to $supplierEmail: ${ex.message}" }
        }.also {
            attachment?.let {
                runCatching {
                    if (it.exists() && it.delete()) {
                        logger.debug { "Temporary attachment deleted: ${it.absolutePath}" }
                    }
                }
            }
        }
    }

    private fun validateIfEmailIsEnabled() {
        if (!isEmailEnabled) warnWithoutStacktrace(EMAIL_NOT_ENABLED_MESSAGE)
    }
}
// Um e-mail seja usado para alimentar N estoques, ou seja, um e-mail reprenta uma matriz que tem N filiais.
// Nesse caso, com um único email é possível atualizar/criar o estoque de outros CNPJs