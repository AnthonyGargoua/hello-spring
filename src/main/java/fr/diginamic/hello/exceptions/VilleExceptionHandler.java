package fr.diginamic.hello.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice, cette classe centralise la gestion des erreurs pour tous les contrôleurs de l'application (au lieu de faire un try/catch dans chaque méthode).

@ControllerAdvice
public class VilleExceptionHandler {

    // @ExceptionHandler(VilleException.class), dès qu'une VilleException est levée n'importe où dans un contrôleur, Spring appelle automatiquement cette méthode pour construire la réponse.
    @ExceptionHandler(VilleException.class)
    public ResponseEntity<String> handleVilleException(VilleException exception) {
        // Je renvoie le message de l'exception avec un code 400 (Bad Request), car ce sont des erreurs liées à des données invalides envoyées par le client.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
