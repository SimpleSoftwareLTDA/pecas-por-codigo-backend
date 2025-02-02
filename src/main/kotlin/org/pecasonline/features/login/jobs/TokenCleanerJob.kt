package org.pecasonline.features.login.jobs

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.features.login.repositories.TokenRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

private val logger = KotlinLogging.logger {}

@Component
class TokenCleanerJob(private val tokenRepository: TokenRepository) {

    //@Scheduled(cron = "0 */30 * * * *") // TODO: Migrar para o Redis, e usar o TTL.
    fun clean() {
        val threshold = Instant.now().minus(5, ChronoUnit.MINUTES)

        tokenRepository.deleteExpired(threshold)
        logger.info { "Tokens: ${tokenRepository.count()}" }
    }
}