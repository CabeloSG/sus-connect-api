package br.com.susconnect.config.openapi;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação OpenAPI (Swagger).
 *
 * Projeto: SUS Connect
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI susConnectOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("SUS Connect API")

                        .description("""
                                Plataforma inteligente para otimização
                                do processo de confirmação de consultas
                                do Sistema Único de Saúde (SUS).
                                """)

                        .version("1.0.0")

                        .contact(new Contact()

                                .name("Equipe SUS Connect")

                                .email("hackathon@fiap.com"))

                        .license(new License()

                                .name("FIAP Hackathon 2026")))

                .externalDocs(new ExternalDocumentation()

                        .description("Repositório do Projeto"));
    }

}