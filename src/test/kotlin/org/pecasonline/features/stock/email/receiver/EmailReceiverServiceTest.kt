package org.pecasonline.features.stock.email.receiver

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import jakarta.mail.Address
import jakarta.mail.Flags
import jakarta.mail.Folder
import jakarta.mail.Message
import jakarta.mail.Part
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.pecasonline.features.stock.IStockService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.test.util.ReflectionTestUtils
import java.io.ByteArrayInputStream
import java.io.File

@ExtendWith(MockKExtension::class)
class EmailReceiverServiceTest {

    @MockK
    private lateinit var stockService: IStockService

    @MockK
    private lateinit var supplierRepository: SupplierRepository

    @MockK
    private lateinit var subscriptionService: SubscriptionService

    @InjectMockKs
    private lateinit var emailReceiverService: EmailReceiverService

    private val validEmailFrom = "fornecedor@teste.com"
    private val validCnpj = "12345678000199"

    @BeforeEach
    fun setup() {
        // As a @Value annotation is used, we inject standard values for testing the service
        ReflectionTestUtils.setField(emailReceiverService, "host", "imap.test.com")
        ReflectionTestUtils.setField(emailReceiverService, "username", "sys")
        ReflectionTestUtils.setField(emailReceiverService, "password", "pwd")
        ReflectionTestUtils.setField(emailReceiverService, "inboxFolder", "INBOX")
        ReflectionTestUtils.setField(emailReceiverService, "processedFolder", "Processed")
    }

    @Test
    @DisplayName("Should successfully process a valid .txt email attachment")
    fun `should process valid txt attachment`() {
        // Arrange
        val message = setupMockEmailMessage(fileName = "estoque.txt", contentType = "multipart/mixed")
        setupMockDependencies()

        // Capture the file passed to stockService to assert on it
        val fileSlot = slot<File>()
        every { stockService.createStock(capture(fileSlot), emailAddress = validEmailFrom, originalFileName = "estoque.txt") } returns Unit

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 1) { stockService.createStock(any(), emailAddress = validEmailFrom, originalFileName = "estoque.txt") }
        verify(exactly = 1) { message.setFlag(Flags.Flag.SEEN, true) }
        
        // Assert the mock content was read properly
        val createdFile = fileSlot.captured
        assertTrue(createdFile.exists())
        assertEquals(validStockContent(), createdFile.readText())
        
        // Clean up temp file
        createdFile.delete()
    }

    @Test
    @DisplayName("Should successfully process a valid .csv email attachment")
    fun `should process valid csv attachment`() {
        // Arrange
        val message = setupMockEmailMessage(fileName = "estoque.csv", contentType = "multipart/mixed")
        setupMockDependencies()

        val fileSlot = slot<File>()
        every { stockService.createStock(capture(fileSlot), emailAddress = validEmailFrom, originalFileName = "estoque.csv") } returns Unit

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 1) { stockService.createStock(any(), emailAddress = validEmailFrom, originalFileName = "estoque.csv") }
        verify(exactly = 1) { message.setFlag(Flags.Flag.SEEN, true) }
        
        val createdFile = fileSlot.captured
        assertTrue(createdFile.exists())
        assertEquals(validStockContent(), createdFile.readText())
        
        createdFile.delete()
    }

    @Test
    @DisplayName("Should ignore email if no attachments are present (not multipart)")
    fun `should ignore non multipart emails`() {
        val message = mockk<MimeMessage>(relaxed = true)
        val address = InternetAddress(validEmailFrom)
        every { message.from } returns arrayOf<Address>(address)
        every { message.isMimeType("multipart/*") } returns false

        setupMockDependencies()

        emailReceiverService.handleReceivedEmail(message)

        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
        verify(exactly = 1) { message.setFlag(Flags.Flag.SEEN, true) }
    }

    @Test
    @DisplayName("Should ignore attachment if size strictly exceeds 50MB")
    fun `should ignore heavy attachments`() {
        // Arrange
        val message = setupMockEmailMessage(fileName = "pesado.csv", contentType = "multipart/mixed", overrideSize = 51 * 1024 * 1024)
        setupMockDependencies()

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
        verify(exactly = 1) { message.setFlag(Flags.Flag.SEEN, true) }
    }

    @Test
    @DisplayName("Should not process file if it has invalid structure content")
    fun `should not process invalid structure`() {
        // Arrange
        val message = setupMockEmailMessage(fileName = "estoque.txt", contentType = "multipart/mixed", customContent = "CABEÇALHO ERRADO\nSEM DADOS\n")
        setupMockDependencies()

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
        verify(exactly = 1) { message.setFlag(Flags.Flag.SEEN, true) }
    }

    @Test
    @DisplayName("Should fail and log error if sender email is not registered")
    fun `should fail if sender is not registered`() {
        val message = setupMockEmailMessage(fileName = "estoque.txt", contentType = "multipart/mixed")
        val unregisteredEmail = "unregistered@teste.com"
        val address = InternetAddress(unregisteredEmail)
        every { message.from } returns arrayOf<Address>(address)
        
        every { supplierRepository.findSupplierCnpjByEmail(unregisteredEmail) } returns null

        val exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException::class.java) {
            emailReceiverService.handleReceivedEmail(message)
        }
        assertEquals("CNPJ não cadastrado", exception.message)

        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { message.setFlag(Flags.Flag.SEEN, true) }
    }

    @Test
    @DisplayName("Should fail if supplier subscription is inactive")
    fun `should fail if subscription is inactive`() {
        val message = setupMockEmailMessage(fileName = "estoque.txt", contentType = "multipart/mixed")
        
        val mockSupplier = mockk<Supplier>()
        every { supplierRepository.findSupplierCnpjByEmail(validEmailFrom) } returns validCnpj
        every { supplierRepository.findSupplierByEmail(validEmailFrom) } returns mockSupplier
        // Simulate exception thrown by subscription service
        every { 
            subscriptionService.checkIfSubscriptionIsActiveOrThrow(mockSupplier, validCnpj) 
        } throws RuntimeException("Subscription inactive")

        val exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException::class.java) {
            emailReceiverService.handleReceivedEmail(message)
        }
        assertEquals("Subscription inactive", exception.message)

        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
        verify(exactly = 0) { message.setFlag(Flags.Flag.SEEN, true) }
    }

    private fun setupMockDependencies() {
        val mockSupplier = mockk<Supplier>()
        every { supplierRepository.findSupplierCnpjByEmail(validEmailFrom) } returns validCnpj
        every { supplierRepository.findSupplierByEmail(validEmailFrom) } returns mockSupplier
        every { subscriptionService.checkIfSubscriptionIsActiveOrThrow(mockSupplier, validCnpj) } returns Unit
    }

    private fun setupMockEmailMessage(fileName: String, contentType: String, customContent: String? = null, overrideSize: Int = 1024): Message {
        val message = mockk<MimeMessage>(relaxed = true)
        
        // Mock From Address
        val address = InternetAddress(validEmailFrom)
        every { message.from } returns arrayOf<Address>(address)
        every { message.subject } returns "Envio de Estoque - $fileName"
        
        // Mock Content Type checking
        every { message.isMimeType("multipart/*") } returns contentType.startsWith("multipart")
        
        // Mock Folder environment for moving the message
        val folder = mockk<Folder>(relaxed = true)
        val store = mockk<jakarta.mail.Store>(relaxed = true)
        val processedFolder = mockk<Folder>(relaxed = true)
        
        every { message.folder } returns folder
        every { folder.isOpen } returns true
        every { folder.store } returns store
        every { store.getFolder("Processed") } returns processedFolder
        every { processedFolder.exists() } returns true

        // Mock Multipart content
        val multipart = mockk<MimeMultipart>()
        every { message.content } returns multipart
        
        val bodyPart = mockk<MimeBodyPart>()
        every { multipart.count } returns 1
        every { multipart.getBodyPart(0) } returns bodyPart
        
        // Mock Body Part details
        every { bodyPart.size } returns overrideSize
        every { bodyPart.disposition } returns Part.ATTACHMENT
        every { bodyPart.fileName } returns fileName
        
        val contentBytes = (customContent ?: validStockContent()).toByteArray()
        every { bodyPart.inputStream } returns ByteArrayInputStream(contentBytes)

        return message
    }

    private fun validStockContent(): String {
        return """
            COD1;10;100.50;Extra
            COD2;5;50.00;Extra
            COD3;20;25.99;Extra
        """.trimIndent()
    }
}
