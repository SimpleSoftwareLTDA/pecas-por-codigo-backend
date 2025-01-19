package org.pecasonline.features.login.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "O e-mail não pode estar vazio.")
    @field:Email(message = "O e-mail informado não é válido.")
    val email: String
)