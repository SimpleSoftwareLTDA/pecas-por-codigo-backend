package org.pecasonline.features.login.controllers

import org.pecasonline.features.login.dto.LoginRequest
import org.pecasonline.features.login.dto.TokenResponse
import org.pecasonline.features.login.service.MagicLinkService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class LoginController(
    private val magicLinkService: MagicLinkService
) {

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val token = magicLinkService.sendLoginLinkWithToken(request.email)

        return ResponseEntity.status(HttpStatus.CREATED).body(TokenResponse("https://www.pecasporcodigo.com.br/auth/${token}"))
    }

    @GetMapping("/login/verify")
    fun verifyToken(@RequestParam token: String): ResponseEntity<Any> = magicLinkService.checkIsValidTokenAndSubscriptionActive(token)
}
