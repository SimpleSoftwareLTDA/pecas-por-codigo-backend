package org.pecasonline.features.login.service

import org.pecasonline.common.SecureRandomSingleton
import org.pecasonline.features.login.entities.TokenRepository
import org.pecasonline.features.login.entities.Tokens
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.entities.InvalidSubscriptionException
import org.pecasonline.features.subscription.entities.InvalidTokenException
import org.pecasonline.features.subscription.entities.PaymentLateException
import org.pecasonline.features.subscription.entities.SubscriptionInactiveException
import org.pecasonline.features.subscription.entities.SubscriptionStatus
import org.pecasonline.features.subscription.entities.SupplierNotFoundException
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.ContactRepository
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class MagicLinkService(
    private val tokenRepository: TokenRepository,
    private val emailSenderService: EmailSenderService,
    private val contactRepository: ContactRepository,
    private val supplierRepository: SupplierRepository
) {

    private val random = SecureRandomSingleton.instance

    private val alphabet = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    @Transactional
    fun sendLoginLinkWithToken(email: String): String {
        var returnTokenTemp = ""

        when {
            checkSupplierEmail(email) -> {
                val supplier = supplierRepository.findSupplierByEmail(email)

                supplier?.let {
                    when {
                        supplierExists(supplier) -> {
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
                        }

                        else -> error("Fornecedor não encontrado para o e-mail $email. Magic Link não será gerado.")
                    }
                }
            }

            else -> error("E-mail não cadastrado. Magic Link não será gerado para ele.")
        }
        return returnTokenTemp
    }

    private fun supplierExists(supplier: Supplier?) = supplier != null


    fun token(size: Int = 64): String = (1..size).map { alphabet[random.nextInt(alphabet.size)] }.joinToString("")

    @Transactional(rollbackFor = [RuntimeException::class])
    fun checkIsValidTokenAndSubscriptionActive(token: String): ResponseEntity<Any> {
        val tokenEntity = tokenRepository.findByToken(token) ?: throw InvalidTokenException()

        val supplier = tokenEntity.supplier ?: throw SupplierNotFoundException()

        return when (supplier.subscription?.status) {
            SubscriptionStatus.ACTIVE -> ResponseEntity.ok(mapOf("status" to 200, "message" to "Acesso permitido"))
            SubscriptionStatus.INACTIVE -> throw SubscriptionInactiveException()
            SubscriptionStatus.LATE -> throw PaymentLateException()

            else -> throw InvalidSubscriptionException()
        }
    }


    fun getTokenOwner(token: String): String? = tokenRepository.findSupplierCnpjByToken(token)

    private fun checkSupplierEmail(email: String): Boolean = contactRepository.existsByItemsEmail(email)

}
