package org.pecasonline.features.stock.email.receiver

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.Flags.Flag
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.Part.ATTACHMENT
import jakarta.mail.internet.MimeMultipart
import org.pecasonline.features.stock.IStockService
import org.pecasonline.features.stock.email.receiver.RegexPatterns.costRegex
import org.pecasonline.features.stock.email.receiver.RegexPatterns.emailRegex
import org.pecasonline.features.stock.email.receiver.RegexPatterns.productCodeRegex
import org.pecasonline.features.stock.email.receiver.RegexPatterns.quantityRegex
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.TEXT_PLAIN_VALUE
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

private val logger = KotlinLogging.logger {}

@Service
class EmailReceiverService(
    private val stockService: IStockService,
    private val supplierRepository: SupplierRepository,
    private val subscriptionService: SubscriptionService,
    @Value("\${spring.mail.properties.mail.imap.host}") private val host: String,
    @Value("\${spring.mail.username}") private val username: String,
    @Value("\${spring.mail.password}") private val password: String,
    @Value("\${spring.mail.imap.inbox-folder:INBOX}") private val inboxFolder: String,
    @Value("\${spring.mail.imap.processed-folder:}") private val processedFolder: String
) {

    fun handleReceivedEmail(message: Message) {
        logger.info { "Processando mensagem: ${message.subject}" }

        if (!hasValidAttachment(message)) {
            logger.info { "E-mail ignorado: não contém anexo .txt ou .csv" }
            return
        }

        val senderEmail = message.from.firstOrNull()?.toString()?.let { extractEmailAddress(it) } ?: "E-Mail Desconhecido"

        supplierRepository.findSupplierCnpjByEmail(senderEmail)?.let { cnpj ->
            logger.info { "Fornecedor identificado com sucesso: $senderEmail (CNPJ: $cnpj)" }

            supplierRepository.findSupplierByEmail(senderEmail)?.let { supplier ->
                subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)
            }

            processAttachments(message, senderEmail = senderEmail, supplierCnpj = cnpj)

            message.setFlag(Flag.SEEN, true)

            if (processedFolder.isNotEmpty()) {
                val folder = message.folder
                if (folder != null && folder.isOpen) {
                    val store = folder.store
                    val processed = store.getFolder(processedFolder)

                    if (!processed.exists()) {
                        processed.create(Folder.HOLDS_MESSAGES)
                    }

                    folder.copyMessages(arrayOf(message), processed)
                    logger.info { "E-mail '${message.subject}' movido para a pasta $processedFolder" }
                } else {
                    logger.warn { "Não foi possível mover a mensagem pois a pasta está fechada ou nula." }
                }
            }
        } ?: error("CNPJ não cadastrado")
    }

    private fun hasValidAttachment(message: Message): Boolean {
        if (!message.isMimeType("multipart/*")) return false

        return runCatching {
            val multipart = message.content as MimeMultipart
            for (i in 0 until multipart.count) {
                val bodyPart = multipart.getBodyPart(i)
                if (ATTACHMENT.equals(bodyPart.disposition, ignoreCase = true)) {
                    val fileName = bodyPart.fileName?.lowercase() ?: ""
                    if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
                        return@runCatching true
                    }
                }
            }
            false
        }.getOrDefault(false)
    }

    private fun processAttachments(message: Message, senderEmail: String, supplierCnpj: String?) {

        supplierCnpj?.let { _ ->
            if (message.isMimeType("multipart/*")) {
                val multipart = message.content as MimeMultipart

                for (i in 0 until multipart.count) {
                    val bodyPart = multipart.getBodyPart(i)

                    when {
                        bodyPart.size > 50 * 1024 * 1024 -> { // Limite de 50 MB
                            logger.error { $$"Arquivo muito grande para ser processado: ${bodyPart.size} bytes" }
                            return
                        }

                        ATTACHMENT.equals(bodyPart.disposition, ignoreCase = true) -> {
                            val fileName = bodyPart.fileName

                            if (fileName.endsWith(".txt") || fileName.endsWith(".csv")) {
                                logger.info { "Arquivo anexo: $fileName (${bodyPart.size / 1024} KB) aprovado para processamento." }

                                // Read attachment once and convert to UTF-8 if needed (handles ANSI/Windows-1252)
                                val originalBytes = bodyPart.inputStream.use { it.readBytes() }
                                val utf8Bytes = org.pecasonline.common.encoding.EncodingUtils.toUtf8Bytes(originalBytes)

                                logger.info { "Enviando arquivo para o StockService processar..." }

                                runCatching {
                                    val tempFile = File.createTempFile("upload-", "-$fileName")
                                    tempFile.outputStream().use { it.write(utf8Bytes) }

                                    stockService.createStock(
                                        file = tempFile,
                                        emailAddress = senderEmail,
                                        originalFileName = fileName
                                    )
                                }.onFailure { ex ->
                                    logger.error(ex) { "Erro ao processar arquivo" }
                                }
                            }
                        }
                    }
                }
            }
        } ?: logger.error { "CNPJ e E-MAIL não cadastrados" }
    }
}

object RegexPatterns {
    val productCodeRegex = Pattern.compile("[a-zA-Z0-9.\\- ]+")
    val quantityRegex = Pattern.compile("\\d+")
    // Allow prices with optional thousand separators and either ',' or '.' as decimal separator
    // Examples: 89,427.27 | 1.005,47 | 999.86 | 999,86 | 1000
    val costRegex = Pattern.compile("\\d[\\d.,]*(?:[.,]\\d{2})?")
    val emailRegex = Pattern.compile("<(.*?)>|([\\w.-]+@[\\w.-]+\\.[\\w]{2,})").matcher("")
    // Split fields only by tab or semicolon as per specification, or 2 or more spaces
    val whitespaceRegex = Pattern.compile("[\\t;]+|\\s{2,}")
}

class CustomMultipartFile(
    private val name: String,
    private val originalFilename: String?,
    private val contentType: String?,
    private val content: ByteArray
) : MultipartFile {

    override fun getName(): String = name

    override fun getOriginalFilename(): String? = originalFilename

    override fun getContentType(): String? = contentType

    override fun isEmpty(): Boolean = content.isEmpty()

    override fun getSize(): Long = content.size.toLong()

    override fun getBytes(): ByteArray = content

    override fun getInputStream(): InputStream = ByteArrayInputStream(content)

    override fun transferTo(dest: File) {
        dest.outputStream().use { it.write(content) }
    }
}

fun stringToMultipartFile(content: String, fileName: String): MultipartFile = CustomMultipartFile(
    name = fileName,
    originalFilename = fileName,
    contentType = TEXT_PLAIN_VALUE,
    content = content.toByteArray(StandardCharsets.UTF_8)
)

fun streamToFile(inputStream: InputStream, fileName: String): File {
    val tempFile = File.createTempFile("upload-", "-$fileName")

    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}

fun extractEmailAddress(input: String): String? {
    emailRegex.reset(input)

    return when {
        emailRegex.find() -> {
            val emailWithBrackets = emailRegex.group(1)
            val emailWithoutBrackets = emailRegex.group(2)
            emailWithBrackets ?: emailWithoutBrackets
        }
        else -> error("Um formato inesperado de endereço de e-mail foi informado, verificar o Regex.")
    }
}
