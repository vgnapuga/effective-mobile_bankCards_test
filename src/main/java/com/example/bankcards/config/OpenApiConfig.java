package com.example.bankcards.config;


import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@Configuration
@OpenAPIDefinition(info = @Info(title = "Bank Cards Management API", version = "1.0.0", description = "API для управления банковскими картами", contact = @Contact(name = "API Support", email = "nshavrin50@gmail.com")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class OpenApiConfig {
}
