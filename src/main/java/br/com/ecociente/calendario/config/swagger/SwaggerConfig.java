package br.com.ecociente.calendario.config.swagger;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Calendário de Coletas",
                version = "1.0.0",
                description = """
                                API  para consulta de agendamnetos de coleta.
                                O conteúdo retornado é definido pelo perfil do usuário autenticado:
                                -SINDICO: agendamentos dos próprios condomínios;
                                -COOPERATIVA: agendamentos associados á própria cooperativa
                                """,
                contact = @Contact(
                        name = "Equipe EcoCiente")),
        servers = {
                @Server(
                        url = "http://localhost:9800",
                        description = "Ambiente local"
                )
        },
        tags = {
                @Tag(
                      name = "Agendamentos",
                      description = "Consultas de agendamnetos de coleta" )
        })
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT")
public class SwaggerConfig {
}