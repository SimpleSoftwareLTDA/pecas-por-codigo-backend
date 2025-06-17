package org.pecasonline.common

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.HexFormat

object Constants {
    const val BASE_ENDPOINT = "/api/v1"
    const val CONTACT_EMAIL = "contato@pecasporcodigo.com.br"
    const val DEFAULT_GROUP_NAME = "Peças Por Código"
    val DEFAULT_BANNER_URL = listOf(
        "https://i.postimg.cc/mDWSpSh2/Memorial-Pe-as-a-escolha-de-quem-busca-excel-ncia-em-cada-detalhe-Entendemos-a-paix-o-do-brasileiro.png"
    )
    const val DEFAULT_FILE_NAME = "Arquivo de estoque"
    const val OLD_COMPETITOR_URL = "http://www.pecas-on-line.com.br"
    const val INVALID_CNPJ = "CNPJ inválido"

    const val CAP_SOLVER_API_KEY = "CAP-64429D47F557325A48251FD98934E6AB3F448F491FE115EAF5C545D3977B5C96"
    const val SITE_KEY = "6LfqaC0rAAAAABIEZhJI-p5ttUThxV9UFe-i5QTk"
    const val SITE_URL = "$OLD_COMPETITOR_URL/consultacod.php"
}

object SecureRandomSingleton {
    val instance: SecureRandom by lazy { SecureRandom() }
}

object HashGenerator {
    val HEX_FORMAT: HexFormat = HexFormat.of()
    val MD5_DIGEST: MessageDigest by lazy { MessageDigest.getInstance("MD5") }
}
