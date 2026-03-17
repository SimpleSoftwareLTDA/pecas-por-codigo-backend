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
    private val emailReceiverService: EmailReceiverService
) {

    @Bean
    fun imapMailReceiver(): ImapMailReceiver {
        val encodedUsername = URLEncoder.encode(username, "UTF-8")
        val encodedPassword = URLEncoder.encode(password, "UTF-8")
        val url = "imaps://$encodedUsername:$encodedPassword@$host/$inboxFolder"
        
        val receiver = ImapMailReceiver(url)
        receiver.isShouldMarkMessagesAsRead = true
        receiver.setShouldDeleteMessages(false)
        receiver.setAutoCloseFolder(false)
        receiver.setSearchTermStrategy { supportedFlags, folder ->
            FlagTerm(Flags(Flags.Flag.SEEN), false)
        }
        
        val props = Properties()
        props.setProperty("mail.imap.connectiontimeout", "60000")
        props.setProperty("mail.imap.timeout", "60000")
        props.setProperty("mail.imaps.connectiontimeout", "60000")
        props.setProperty("mail.imaps.timeout", "60000")
        props.setProperty("mail.imaps.partialfetch", "false")
        receiver.setJavaMailProperties(props)
        
        return receiver
    }

    @Bean
    fun imapIdleFlow(imapMailReceiver: ImapMailReceiver): IntegrationFlow {
        return IntegrationFlow.from(
            ImapIdleChannelAdapter(imapMailReceiver).apply {
                this.setAutoStartup(true)
            }
        ).handle { message: org.springframework.messaging.Message<*> ->
            runCatching {
                val mailMessage = message.payload as jakarta.mail.Message
                emailReceiverService.handleReceivedEmail(mailMessage)
            }.onFailure { ex ->
                logger.error(ex) { "Erro ao processar e-mail no IMAP IDLE" }
            }
        }.get()
    }
}
