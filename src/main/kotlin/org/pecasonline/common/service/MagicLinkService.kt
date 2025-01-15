package org.pecasonline.common.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class MagicLinkService(
    private val users: UserDetailsService,
    private val tokenRepository: TokenRepository
) {

    private val random = SecureRandomSingleton.instance

    private val alphabet = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')

    private val strategy = SecurityContextHolder.getContextHolderStrategy()
    private val sessionRepository = HttpSessionSecurityContextRepository()

    @Transactional
    fun authenticate(token: String, request: HttpServletRequest, response: HttpServletResponse) {
        val entity = tokenRepository.findByToken(token)

        entity?.let {
            val user = users.loadUserByUsername(entity.username)
            val authentication = UsernamePasswordAuthenticationToken(user, user.password, user.authorities)
            val context = strategy.context

            context.authentication = authentication
            strategy.context = context

            sessionRepository.saveContext(context, request, response)

            tokenRepository.deleteById(entity.id!!)
        }
    }


    fun issueToken(username: String) {
        val user = users.loadUserByUsername(username)

        val tokens = tokenRepository.save(Tokens().apply {
            this.username = user.username
            this.token = token()
            this.created = Instant.now()
        })

        mail(tokens)
    }

    private fun mail(tokens: Tokens) {
        logger.info { "Enviou o email com o token: ${tokens.token} e o username: ${tokens.username} -> http://localhost:8080/auth/${tokens.token}" }
    }

    fun token(size: Int = 64): String = (1..size).map { alphabet[random.nextInt(alphabet.size)] }.joinToString("")

    fun validateToken(token: String): Boolean {
        val tokenEntity = tokenRepository.findByToken(token)

        return tokenEntity != null
    }

}

object SecureRandomSingleton {
    val instance: SecureRandom by lazy {  SecureRandom() }
}
