package org.pecasonline.common.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import io.getunleash.DefaultUnleash
import io.getunleash.FakeUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig as SDKConfig
import org.springframework.beans.factory.annotation.Value

@Configuration
class UnleashConfig {
    private val logger = LoggerFactory.getLogger(UnleashConfig::class.java)

    fun unleash(
        @Value("\${unleash.api-url}") apiUrl: String,
        @Value("\${unleash.api-token}") apiToken: String,
        @Value("\${unleash.app-name}") appName: String,
        @Value("\${unleash.instance-id}") instanceId: String,
        @Value("\${unleash.environment}") environment: String
    ): Unleash{
        return try {
            val config = SDKConfig.builder()
                .appName(appName)
                .instanceId(instanceId)
                .unleashAPI(apiUrl)
                .apiKey(apiToken)
                .environment(environment)
                .synchronousFetchOnInitialisation(false)
                .build()
            logger.info("Starting Unleash Client for aplication: $appName environment: $environment")
            DefaultUnleash(config)
        }catch (e: Exception){
            logger.error("Fatal error on init Unleash. Using FakeUnleash (No-Op). Error: ${e.message}");
            FakeUnleash()
        }
    }
}