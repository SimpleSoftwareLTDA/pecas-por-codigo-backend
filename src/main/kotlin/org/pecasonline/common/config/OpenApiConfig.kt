package org.pecasonline.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value("\${app.url}")
    private lateinit var currentURL: String

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Peças Por Código API")
                    .description("API de Catalogo de peças automotivas e seus fornecedores.")
                    .version("1.0")
            )
            .addServersItem(Server().url(currentURL))
    }
}
