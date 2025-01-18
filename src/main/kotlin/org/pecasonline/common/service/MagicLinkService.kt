package org.pecasonline.common.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.supplier.repository.ContactRepository
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class MagicLinkService(
    private val users: UserDetailsService,
    private val tokenRepository: TokenRepository,
    private val emailSenderService: EmailSenderService,
    private val contactRepository: ContactRepository,
    private val supplierRepository: SupplierRepository
) {

    private val random = SecureRandomSingleton.instance

    private val alphabet = ('a' .. 'z') + ('A' .. 'Z') + ('0' .. '9')

    @Transactional
    fun sendLoginLinkWithToken(email: String): String {
        var returnTokenTemp = ""

        when {
            checkSupplierEmail(email) -> {
                val supplier = supplierRepository.findSupplierByEmail(email)

                if (supplier != null) {
                    val tokens = tokenRepository.save(
                        Tokens()
                            .apply {
                                this.username = email
                                this.token = token()
                                this.created = Instant.now()
                                this.supplier = supplier
                            }
                    )

                    returnTokenTemp = tokens.token

                    emailSenderService.sendMagicLink(supplierEmail = email, token = tokens.token, supplierName = supplier.name)
                } else error("Fornecedor não encontrado para o e-mail $email. Magic Link não será gerado.")
            }
            else -> error("E-mail não cadastrado. Magic Link não será gerado para ele.")
        }
        return returnTokenTemp
    }


    fun token(size: Int = 64): String = (1..size).map { alphabet[random.nextInt(alphabet.size)] }.joinToString("")

    fun checkIsValidToken(token: String): Boolean {
        val tokenEntity = tokenRepository.findByToken(token)

        return tokenEntity != null
    }

    fun getTokenOwner(token: String): String? = tokenRepository.findSupplierCnpjByToken(token)

    private fun checkSupplierEmail(email: String): Boolean = contactRepository.existsByItemsEmail(email)

}

object SecureRandomSingleton {
    val instance: SecureRandom by lazy {  SecureRandom() }
}
