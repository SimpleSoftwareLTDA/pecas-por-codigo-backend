package org.pecasonline.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**") // Allows CORS on all paths
            .allowedOrigins("*") // Accepts requests from any origin
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allows all common HTTP methods
            .allowedHeaders("*") // Accepts all headers
    }
}

