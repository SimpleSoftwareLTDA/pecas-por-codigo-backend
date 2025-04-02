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
    val DEFAULT_BANNER_URL = listOf(
        "https://banners.pecasonlinex.com.br/Teste.png",
        "https://i.postimg.cc/mDWSpSh2/Memorial-Pe-as-a-escolha-de-quem-busca-excel-ncia-em-cada-detalhe-Entendemos-a-paix-o-do-brasileiro.png"
    )
}

object SecureRandomSingleton {
    val instance: SecureRandom by lazy { SecureRandom() }
}

object HashGenerator {
    val HEX_FORMAT: HexFormat = HexFormat.of()
    val MD5_DIGEST: MessageDigest by lazy { MessageDigest.getInstance("MD5") }
}
