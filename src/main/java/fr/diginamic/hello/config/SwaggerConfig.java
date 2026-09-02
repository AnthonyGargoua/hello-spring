package fr.diginamic.hello.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation Swagger/OpenAPI de l'API.
 */
@Configuration
public class SwaggerConfig {

    // Ce bean définit la "carte d'identité" de notre API,
    // affichée en haut de la page Swagger
    /**
     * Définit les informations générales (titre, version, description, contact) affichées sur la page Swagger.
     *
     * @return la configuration OpenAPI de l'API
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SPRING BOOT – TP N°11")
                        .version("1.0")
                        .description("MISE EN PLACE DE SWAGGER - DIGINAMIC")
                        .contact(new Contact()
                                .name("Anthony")
                                .email("gargouaanthony@hotmail.fr")));
    }
}