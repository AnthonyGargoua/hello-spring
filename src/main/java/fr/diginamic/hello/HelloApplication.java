package fr.diginamic.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application Spring Boot.
 */
@SpringBootApplication
public class HelloApplication {

	/**
	 * Démarre l'application Spring Boot.
	 *
	 * @param args arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}
