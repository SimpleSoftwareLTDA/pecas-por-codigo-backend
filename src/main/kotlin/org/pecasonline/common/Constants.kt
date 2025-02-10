package org.pecasonline.common

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.HexFormat

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

object HashGenerator {
    val HEX_FORMAT: HexFormat = HexFormat.of()
    val MD5_DIGEST: MessageDigest by lazy { MessageDigest.getInstance("MD5") }
}
