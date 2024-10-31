package org.pecasonline.common.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(request: HttpServletRequest): OpenAPI {
        val serverUrl = ServletUriComponentsBuilder.fromRequestUri(request)
            .scheme("https")
            .replacePath(null)
            .build()
            .toUriString()

        return OpenAPI()
            .addServersItem(Server().url(serverUrl))
            .info(
                Info().title("Novo Pecas Online API")
                    .description("API para gerenciamento de peças automotivas.")
                    .version("1.0")
            )
    }
}
