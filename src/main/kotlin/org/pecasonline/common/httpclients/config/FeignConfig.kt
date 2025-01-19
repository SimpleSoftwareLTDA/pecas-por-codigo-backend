package org.pecasonline.common.httpclients.config

import feign.RequestInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig(
    private val asaasProperties: AsaasProperties
) {

    @Bean
    fun requestInterceptor(): RequestInterceptor =
        RequestInterceptor { template ->
            template.header("accept", "application/json")
            template.header("content-type", "application/json")
            template.header("access_token", asaasProperties.apiKey)
        }
}