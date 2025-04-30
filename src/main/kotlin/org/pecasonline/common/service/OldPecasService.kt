package org.pecasonline.common.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.httpclients.OldCompetitorClient
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class OldPecasService(
    private val pecasOnlineFeignClient: OldCompetitorClient
) {
    fun buscarPecasNoAntigo(partNumber: String): String {
        return runCatching {
            pecasOnlineFeignClient.consultarPeca(partNumber = partNumber)
        }.onSuccess {
            logger.info { "Successfully fetched part $partNumber" }
        }.onFailure {
            logger.error(it) { "Failed to fetch part $partNumber" }
        }.getOrElse { "" }

    }
}