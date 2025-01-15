package org.pecasonline.common.auth

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.pecasonline.common.service.MagicLinkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Async
import org.springframework.web.bind.annotation.*
import java.security.Principal

private val logger = KotlinLogging.logger {}

@RestController
class LoginController(
    private val magicLinkService: MagicLinkService
) {

    @GetMapping("/me")
    fun me(principal: Principal): String = "Hello, ${principal.name}"

    @Async
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest) {

        runCatching {
            magicLinkService.issueToken(request.email)
        }.onFailure { ex ->
            logger.error(ex) { "Usuário não cadastrado" }
        }
    }

    @GetMapping("/auth")
    fun authenticate(@RequestParam token: String, request: HttpServletRequest, response: HttpServletResponse) {
        magicLinkService.authenticate(token, request, response)
    }

    @GetMapping("/login/verify")
    fun verifyToken(@RequestParam token: String): ResponseEntity<String> =
        when {
            magicLinkService.validateToken(token) -> ResponseEntity.ok("Usuário autenticado com sucesso")

            else -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou expirado.")
        }


}

data class LoginRequest(
    @field:NotBlank(message = "O e-mail não pode estar vazio.")
    @field:Email(message = "O e-mail informado não é válido.")
    val email: String
)