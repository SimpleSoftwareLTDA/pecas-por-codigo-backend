package org.pecasonline.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import org.springframework.core.env.Environment

@Configuration
class OpenApiConfig(private val env: Environment) {

    @Value("\${app.url}")
    private lateinit var currentURL: String

    @Bean
    fun customOpenAPI(): OpenAPI {
        val finalURL = if (currentURL.isNotBlank() && !currentURL.contains("localhost")) {
            currentURL
        } else if (env.activeProfiles.contains("prod")) {
            "https://backend.pecasporcodigo.com.br/"
        } else {
            "http://localhost:8080"
        }

        println("OpenApiConfig: Final Swagger URL is: $finalURL")
        
        return OpenAPI()
            .info(
                Info().title("Peças Por Código API")
                    .description("API de Catalogo de peças automotivas e seus fornecedores.")
                    .version("1.0")
            )
            .servers(listOf(Server().url(finalURL)))
    }
}
