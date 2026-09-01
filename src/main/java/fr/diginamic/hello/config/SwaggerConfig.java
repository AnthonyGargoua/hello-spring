package fr.diginamic.hello.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // Ce bean définit la "carte d'identité" de notre API,
    // affichée en haut de la page Swagger
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