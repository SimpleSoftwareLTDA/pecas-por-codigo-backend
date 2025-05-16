package org.pecasonline.common.service

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.OkHttpClient
import org.pecasonline.common.httpclients.ReCaptchaV2
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OldPecasService(val blah: OkHttpClient) {

    @Cacheable(value = ["ReCaptchaV2"], key = "#code")
    fun buscarPecasNoAntigo(code: String): String {
        return runCatching {
            val captchaTokenResult = ReCaptchaV2(blah).capSolver(code)

            when {
                captchaTokenResult.isBlank() -> {
                    logger.warn { "Failed to obtain reCAPTCHA token." }
                    return ""
                }
                else -> {
                    val searchResult = ReCaptchaV2(blah).performSearch(recaptchaToken = captchaTokenResult, code = code)
                    searchResult
                }
            }
        }.onSuccess {
            logger.info { "Successfully fetched part $code" }
        }.onFailure {
            logger.error(it) { "Failed to fetch part $code" }
        }.getOrElse { "" }
    }
}