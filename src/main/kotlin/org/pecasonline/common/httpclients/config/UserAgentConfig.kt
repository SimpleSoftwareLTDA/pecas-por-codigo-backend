package org.pecasonline.common.httpclients.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UserAgentConfig {

    @Bean
    fun chromeUserAgentInterceptor(): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            template.header(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            )
        }
    }
}