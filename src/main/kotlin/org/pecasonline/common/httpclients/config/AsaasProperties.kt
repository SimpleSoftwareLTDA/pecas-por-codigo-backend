package org.pecasonline.common.httpclients.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "asaas.api")
data class AsaasProperties(

    var apiKey: String = "",
)