package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.configs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme

@OpenAPIDefinition(
    info = Info(
        contact = Contact(
            name = "Janssen Batista",
            email = "batistajanssen.dev@gmail.com",
            url = "https://github.com/janssenbatista"
        ),
        title = "JuMarket API",
        version = "1.0.0"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Authentication",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    `in` = SecuritySchemeIn.HEADER
)
class OpenApiConfiguration {
}