package org.pecasonline.features.stock.email.receiver

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.mail.ImapIdleChannelAdapter
import org.springframework.integration.mail.ImapMailReceiver
import java.net.URLEncoder
import java.util.Properties
import jakarta.mail.Flags
import jakarta.mail.search.FlagTerm

private val logger = KotlinLogging.logger {}

@Configuration
class EmailIntegrationConfig(
    @Value("\${spring.mail.properties.mail.imap.host}") private val host: String,
    @Value("\${spring.mail.username}") private val username: String,
    @Value("\${spring.mail.password}") private val password: String,
    @Value("\${spring.mail.imap.inbox-folder:INBOX}") private val inboxFolder: String,
    @Value("\${spring.mail.imap.spam-folder:Junk}") private val spamFolder: String,
    private val emailReceiverService: EmailReceiverService
) {

    private fun createReceiver(folder: String): ImapMailReceiver {
        val encodedUsername = URLEncoder.encode(username, "UTF-8")
        val encodedPassword = URLEncoder.encode(password, "UTF-8")
        val url = "imaps://$encodedUsername:$encodedPassword@$host/$folder"

        val receiver = ImapMailReceiver(url).apply {
            isShouldMarkMessagesAsRead = true
            setShouldDeleteMessages(false)
            setAutoCloseFolder(false)
            setSearchTermStrategy { _, _ ->
                FlagTerm(Flags(Flags.Flag.SEEN), false)
            }
        }

        val props = Properties().apply {
            setProperty("mail.imap.connectiontimeout", "60000")
            setProperty("mail.imap.timeout", "60000")
            setProperty("mail.imaps.connectiontimeout", "60000")
            setProperty("mail.imaps.timeout", "60000")
            setProperty("mail.imaps.partialfetch", "false")
        }
        receiver.setJavaMailProperties(props)

        return receiver
    }

    @Bean
    fun imapMailReceiverInbox(): ImapMailReceiver = createReceiver(inboxFolder)

    @Bean
    fun imapMailReceiverSpam(): ImapMailReceiver = createReceiver(spamFolder)

    private fun createFlow(receiver: ImapMailReceiver, folderName: String): IntegrationFlow =
        IntegrationFlow.from(
            ImapIdleChannelAdapter(receiver).apply {
                this.setAutoStartup(true)
            }
        ).handle { message: org.springframework.messaging.Message<*> ->
            runCatching {
                val mailMessage = message.payload as jakarta.mail.Message

                logger.info { "Processando e-mail da pasta: $folderName" }

                emailReceiverService.handleReceivedEmail(mailMessage)
            }.onFailure { ex ->
                logger.error(ex) { "Erro ao processar e-mail no IMAP IDLE ($folderName)" }
            }
        }.get()

    @Bean
    fun imapIdleFlowInbox(imapMailReceiverInbox: ImapMailReceiver): IntegrationFlow =
        createFlow(imapMailReceiverInbox, "INBOX")

    @Bean
    fun imapIdleFlowSpam(imapMailReceiverSpam: ImapMailReceiver): IntegrationFlow =
        createFlow(imapMailReceiverSpam, "SPAM")
}

