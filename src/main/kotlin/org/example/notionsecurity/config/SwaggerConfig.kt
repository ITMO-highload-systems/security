package org.example.notionsecurity.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SwaggerConfig {
    var schemeName: String = "bearerAuth"
    var bearerFormat: String = "JWT"
    var scheme: String = "bearer"

    @Bean
    fun caseOpenAPI(): OpenAPI {
        return OpenAPI()
            .addSecurityItem(
                SecurityRequirement()
                    .addList(schemeName)
            ).components(
                Components()
                    .addSecuritySchemes(
                        schemeName, SecurityScheme()
                            .name(schemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .bearerFormat(bearerFormat)
                            .`in`(SecurityScheme.In.HEADER)
                            .scheme(scheme)
                    )
            )
    }
}