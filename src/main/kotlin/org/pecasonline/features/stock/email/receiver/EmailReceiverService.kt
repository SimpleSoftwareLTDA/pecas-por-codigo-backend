package org.pecasonline.features.stock.email.receiver

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.Flags.Flag
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.Part.ATTACHMENT
import jakarta.mail.Session
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
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.*
import java.lang.System.getProperties
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

    fun receiveEmails() {
        val session = Session.getDefaultInstance(getProperties(), null)

        val store = session.getStore("imaps")

        store.use { emailStore ->
            emailStore.connect(host, username, password)

            val inbox = emailStore.getFolder(inboxFolder)

            inbox.use { emailInbox ->
                emailInbox.open(Folder.READ_WRITE)

                val messages = emailInbox.messages.filter { it.flags.contains(Flag.SEEN) }

                for (message in messages) {
                    logger.info { "Processando mensagem: ${message.subject}" }

                    val senderEmail = message.from.firstOrNull()?.toString()?.let { extractEmailAddress(it) } ?: "E-Mail Desconhecido"

                    supplierRepository.findSupplierCnpjByEmail(senderEmail)?.let { cnpj ->

                        supplierRepository.findSupplierByEmail(senderEmail)?.let { supplier ->
                            subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)
                        }

                        processAttachments(message, senderEmail = senderEmail, supplierCnpj = cnpj)

                        message.setFlag(Flag.SEEN, true)

                        if (processedFolder.isNotEmpty()) {
                            val processed = emailStore.getFolder(processedFolder)

                            if (!processed.exists()) {
                                processed.create(Folder.HOLDS_MESSAGES)
                            }

                            emailInbox.copyMessages(arrayOf(message), processed)
                        }
                    } ?: error("CNPJ não cadastrado")
                }
            }
        }
    }

    private fun processAttachments(message: Message, senderEmail: String, supplierCnpj: String?) {

        supplierCnpj?.let { cnpj ->
            if (message.isMimeType("multipart/*")) {
                val multipart = message.content as MimeMultipart

                for (i in 0 until multipart.count) {
                    val bodyPart = multipart.getBodyPart(i)

                    when {
                        bodyPart.size > 50 * 1024 * 1024 -> { // Limite de 50 MB
                            logger.error { "Arquivo muito grande para ser processado: \${bodyPart.size} bytes" }
                            return
                        }

                        ATTACHMENT.equals(bodyPart.disposition, ignoreCase = true) -> {
                            val fileName = bodyPart.fileName

                            if (fileName.endsWith(".txt")) {
                                logger.info { "Arquivo .txt: $fileName encontrado no e-mail, verificando se a estrutura dele é válida..." }

                                val inputStream = bodyPart.inputStream
                                val bufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))

                                if (isValidFileStructure(BufferedReader(bufferedReader))) {
                                    logger.info { "Estrutura do arquivo válida. Processando..." }

                                    runCatching {
                                        stockService.createStock(
                                            file = streamToFile(inputStream, fileName),
                                            emailAddress = senderEmail
                                        )
                                    }.onFailure { ex ->
                                        logger.error(ex) { "Erro ao processar arquivo" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } ?: logger.error { "CNPJ e E-MAIL não cadastrados" }
    }

    private fun isValidFileStructure(reader: BufferedReader): Boolean {
        var lineCount = 0

        reader.useLines { lines ->
            for (line in lines) {
                val fields = line.trim().split("\\s+".toRegex())

                when {
                    fields.size != 4 ||
                            !productCodeRegex.matcher(fields[0]).matches() ||
                            !quantityRegex.matcher(fields[1]).matches() ||
                            !costRegex.matcher(fields[2]).matches()
                        -> return false

                    else -> {
                        lineCount++
                        if (lineCount >= 3) break
                    }
                }
            }
        }
        return lineCount >= 3
    }
}


@RestController
@RequestMapping("/email")
class EmailController(
    private val emailReceiverService: EmailReceiverService
) {

    @GetMapping("/receive")
    fun receiveEmails(): ResponseEntity<String> {
        emailReceiverService.receiveEmails()

        return ResponseEntity.ok("E-mails processados com sucesso!")
    }
}

object RegexPatterns {
    val productCodeRegex = Pattern.compile("[a-zA-Z0-9]+")
    val quantityRegex = Pattern.compile("\\d+")
    val costRegex = Pattern.compile("\\d+\\.\\d{2}")
    val emailRegex = Pattern.compile("<(.*?)>|([\\w.-]+@[\\w.-]+\\.[\\w]{2,})").matcher("")
    val whitespaceRegex = Pattern.compile("\\s+")
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
