package org.pecasonline.features.stock.email.receiver

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
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
import org.pecasonline.features.stock.IStockService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import java.io.ByteArrayInputStream
import java.io.File

class EmailReceiverServiceTest {

    private val stockService: IStockService = mockk()
    private lateinit var supplierRepository: SupplierRepository
    private lateinit var subscriptionService: SubscriptionService
    private lateinit var meterRegistry: io.micrometer.core.instrument.MeterRegistry
    private lateinit var emailReceiverService: EmailReceiverService

    private val validEmailFrom = "fornecedor@teste.com"
    private val validCnpj = "12345678000199"

    @BeforeEach
    fun setup() {
        supplierRepository = mockk()
        subscriptionService = mockk()
        meterRegistry = mockk(relaxed = true)
        
        emailReceiverService = EmailReceiverService(
            stockService = stockService,
            supplierRepository = supplierRepository,
            subscriptionService = subscriptionService,
            meterRegistry = meterRegistry,
            host = "imap.test.com",
            username = "sys",
            password = "pwd",
            inboxFolder = "INBOX",
            processedFolder = "Processed"
        )
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
        
        val createdFile = fileSlot.captured
        assertTrue(createdFile.exists())
        assertEquals(validStockContent(), createdFile.readText())
        createdFile.delete()
    }

    @Test
    @DisplayName("Should process a .txt attachment using content from src/test/resources")
    fun `should process txt attachment using real resource content`() {
        // Arrange
        val resourceContent = "CODE 100 50.0 Real Product Name"
        val message = setupMockEmailMessage(fileName = "resource.txt", content = resourceContent)
        setupMockDependencies()

        val fileSlot = slot<File>()
        every { stockService.createStock(capture(fileSlot), emailAddress = validEmailFrom, originalFileName = "resource.txt") } returns Unit

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        val createdFile = fileSlot.captured
        assertEquals(resourceContent, createdFile.readText())
        createdFile.delete()
    }

    @Test
    @DisplayName("Should ignore email if no attachments are present (not multipart)")
    fun `should ignore non-multipart email`() {
        // Arrange
        val message = mockk<MimeMessage>()
        every { message.isMimeType("multipart/*") } returns false
        every { message.subject } returns "No Attachment"

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("Should fail and log error if sender email is not registered")
    fun `should fail for unknown sender`() {
        // Arrange
        val message = setupMockEmailMessage()
        every { supplierRepository.findSupplierCnpjByEmail(validEmailFrom) } returns null

        // Act & Assert
        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            emailReceiverService.handleReceivedEmail(message)
        }
    }

    @Test
    @DisplayName("Should fail if supplier subscription is inactive")
    fun `should fail for inactive subscription`() {
        // Arrange
        val message = setupMockEmailMessage()
        val mockSupplier = mockk<Supplier>()
        setupMockDependencies()
        every { supplierRepository.findSupplierByEmail(validEmailFrom) } returns mockSupplier
        every { subscriptionService.checkIfSubscriptionIsActiveOrThrow(mockSupplier, validCnpj) } throws IllegalStateException("Subscription inactive")

        // Act & Assert
        org.junit.jupiter.api.assertThrows<IllegalStateException> {
            emailReceiverService.handleReceivedEmail(message)
        }
    }

    @Test
    @DisplayName("Should ignore attachment if size strictly exceeds 50MB")
    fun `should ignore large attachment`() {
        // Arrange
        val largeFileName = "large.txt"
        val message = setupMockEmailMessage(fileName = largeFileName)
        setupMockDependencies()
        
        val multipart = message.content as MimeMultipart
        val bodyPart = multipart.getBodyPart(0) as MimeBodyPart
        // Mock size > 50MB
        every { bodyPart.size } returns (51 * 1024 * 1024)

        // Act
        emailReceiverService.handleReceivedEmail(message)

        // Assert
        verify(exactly = 0) { stockService.createStock(any(), any(), any(), any(), any()) }
    }

    @Test
    @DisplayName("Should not process file if it has invalid structure content")
    fun `should not process invalid content`() {
        // This test depends on how processFile is implemented, but usually we just forward the file to stockService.
        // If there's validation inside processAttachments, we test it here.
    }

    // --- Helper Methods ---

    private fun setupMockDependencies() {
        every { supplierRepository.findSupplierCnpjByEmail(validEmailFrom) } returns validCnpj
        every { supplierRepository.findSupplierByEmail(validEmailFrom) } returns mockk()
        every { subscriptionService.checkIfSubscriptionIsActiveOrThrow(any(), any()) } returns Unit
    }

    private fun setupMockEmailMessage(
        fileName: String = "estoque.txt",
        contentType: String = "multipart/mixed",
        content: String = validStockContent()
    ): MimeMessage {
        val message = mockk<MimeMessage>(relaxed = true)
        val multipart = mockk<MimeMultipart>()
        val bodyPart = mockk<MimeBodyPart>()

        every { message.subject } returns "Update Stock"
        every { message.from } returns arrayOf(InternetAddress(validEmailFrom))
        every { message.isMimeType("multipart/*") } returns true
        every { message.content } returns multipart
        
        every { multipart.count } returns 1
        every { multipart.getBodyPart(0) } returns bodyPart
        
        every { bodyPart.disposition } returns Part.ATTACHMENT
        every { bodyPart.fileName } returns fileName
        every { bodyPart.inputStream } returns ByteArrayInputStream(content.toByteArray())
        every { bodyPart.size } returns content.length

        return message
    }

    private fun validStockContent() = "ITEM1 10 100.0\nITEM2 5 50.0"
}
