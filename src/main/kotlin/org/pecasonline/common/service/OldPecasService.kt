package org.pecasonline.common.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.httpclients.ReCaptchaV2.capSolver
import org.pecasonline.common.httpclients.ReCaptchaV2.performSearch
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OldPecasService {

    @Cacheable(value = ["ReCaptchaV2"], key = "#code")
    fun buscarPecasNoAntigo(code: String): String {
        return runCatching {
            val captchaTokenResult = capSolver(code)

            when {
                captchaTokenResult.isBlank() -> {
                    logger.warn { "Failed to obtain reCAPTCHA token." }
                    return ""
                }
                else -> {
                    val searchResult = performSearch(recaptchaToken = captchaTokenResult, code = code)
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