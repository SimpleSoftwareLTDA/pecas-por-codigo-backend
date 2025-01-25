package org.pecasonline.common

import java.security.SecureRandom

object Constants {
    const val BASE_ENDPOINT = "/api/v1"
    const val DEFAULT_GROUP_NAME = "Novo Peças Online"
    const val INVALID_CNPJ = "CNPJ inválido"
    const val DEFAULT_FILE_NAME = "Arquivo de estoque"
    const val CONTACT_EMAIL = "contato@pecasonlinex.com.br"
}

object SecureRandomSingleton {
    val instance: SecureRandom by lazy { SecureRandom() }
}