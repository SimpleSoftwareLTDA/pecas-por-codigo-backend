package org.pecasonline.common.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.pecasonline.common.service.MagicLinkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1")
class LoginController(
    private val magicLinkService: MagicLinkService
) {

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = magicLinkService.sendLoginLinkWithToken(request.email)

        return ResponseEntity.status(HttpStatus.CREATED).body(TokenResponse("https://www.pecasonlinex.com.br/auth/${token}"))
    }

    @GetMapping("/login/verify")
    fun verifyToken(@RequestParam token: String): ResponseEntity<Any> {
        val cnpj = magicLinkService.getTokenOwner(token)

        return when {
            magicLinkService.validateToken(token) -> {
                val response = LoginResponse(cnpj = cnpj ?: "CNPJ não encontrado")
                ResponseEntity.ok(response)
            }
            else -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.")
        }
    }
}

data class LoginRequest(
    @field:NotBlank(message = "O e-mail não pode estar vazio.")
    @field:Email(message = "O e-mail informado não é válido.")
    val email: String
)

data class LoginResponse(
    val cnpj: String
)

data class TokenResponse(
    val token: String
)
