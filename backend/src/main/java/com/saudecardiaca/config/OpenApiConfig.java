package com.saudecardiaca.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Acompanhamento de Saúde Cardíaca")
                        .version("1.0.0")
                        .description("API REST para cadastro de usuários, autenticação e monitoramento de saúde cardíaca. "
                                + "Permite registrar medições de sinais vitais, consultar histórico e gerar relatórios "
                                + "analíticos de saúde cardíaca.")
                        .contact(new Contact()
                                .name("Claudio Coelho, Gabriel Matos, Kaio Sena e Yan Costa")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido via /login")));
    }
}
