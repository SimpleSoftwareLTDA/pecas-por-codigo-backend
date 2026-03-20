package org.pecasonline.features.stock.email.receiver.localtest

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.activation.DataHandler
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import jakarta.mail.util.ByteArrayDataSource
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.stock.email.receiver.EmailReceiverService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.Properties

//@Profile("local")
//@ConditionalOnProperty(prefix = "app.local-test.email-receiver", name = ["enabled"], havingValue = "true")
@RestController
@RequestMapping("$BASE_ENDPOINT/local-test/email-receiver")
@Tag(name = "Local Test - Email Receiver", description = "Endpoints apenas para testes locais")
class LocalEmailReceiverTestController(
    private val emailReceiverService: EmailReceiverService
) {

    data class LocalEmailReceiverResponse(
        val ok: Boolean,
        val fromEmail: String,
        val originalFileName: String,
        val sizeBytes: Int,
        val error: String? = null
    )

    @PostMapping(
        path = ["/process"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun processEmailAttachment(
        @RequestPart("file") file: MultipartFile,
        @RequestParam("fromEmail", defaultValue = "vendas2@woodstockpecas.com.br") fromEmail: String,
        @RequestParam("subject", required = false) subject: String?
    ): ResponseEntity<LocalEmailReceiverResponse> {
        val originalFileName = (file.originalFilename ?: "upload.txt").trim()

        if (!originalFileName.endsWith(".txt", ignoreCase = true) && !originalFileName.endsWith(".csv", ignoreCase = true)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                LocalEmailReceiverResponse(
                    ok = false,
                    fromEmail = fromEmail,
                    originalFileName = originalFileName,
                    sizeBytes = file.size.toInt(),
                    error = "Arquivo deve terminar com .txt ou .csv (EmailReceiverService ignora outros tipos)."
                )
            )
        }

        val message = buildMimeMessageWithAttachment(
            fromEmail = fromEmail,
            subject = subject ?: "Local test upload - $originalFileName",
            fileName = originalFileName,
            bytes = file.bytes,
            contentType = file.contentType ?: MediaType.TEXT_PLAIN_VALUE
        )

        return runCatching {
            emailReceiverService.handleReceivedEmail(message)
            ResponseEntity.status(HttpStatus.ACCEPTED).body(
                LocalEmailReceiverResponse(
                    ok = true,
                    fromEmail = fromEmail,
                    originalFileName = originalFileName,
                    sizeBytes = file.size.toInt()
                )
            )
        }.getOrElse { ex ->
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                LocalEmailReceiverResponse(
                    ok = false,
                    fromEmail = fromEmail,
                    originalFileName = originalFileName,
                    sizeBytes = file.size.toInt(),
                    error = ex.message ?: ex::class.java.name
                )
            )
        }
    }

    private fun buildMimeMessageWithAttachment(
        fromEmail: String,
        subject: String,
        fileName: String,
        bytes: ByteArray,
        contentType: String
    ): MimeMessage {
        val session = Session.getInstance(Properties())
        val message = MimeMessage(session)

        message.setFrom(InternetAddress(fromEmail))
        message.subject = subject

        val attachmentPart = MimeBodyPart().apply {
            disposition = jakarta.mail.Part.ATTACHMENT
            this.fileName = fileName
            dataHandler = DataHandler(ByteArrayDataSource(bytes, contentType))
        }

        val multipart = MimeMultipart().apply {
            addBodyPart(attachmentPart)
        }

        message.setContent(multipart)
        message.saveChanges()
        return message
    }
}

