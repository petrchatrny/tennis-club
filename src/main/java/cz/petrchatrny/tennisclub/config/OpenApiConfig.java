package cz.petrchatrny.tennisclub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger documentation configuration
 */
@Configuration
public class OpenApiConfig {
    public static final String SECURITY_SCHEME = "bearer-jwt";
    private final SecurityScheme jwtScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME, jwtScheme))
                .info(new Info()
                        .title("Tennis club")
                        .version("1.0")
                        .description("This project is a Java assignment from inQool.")
                        .contact(new Contact().name("developer - Petr Chatrný").email("p.chatrny@protonmail.com"))
                );
    }
}