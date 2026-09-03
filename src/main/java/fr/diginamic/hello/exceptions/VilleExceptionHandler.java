package fr.diginamic.hello.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

// @ControllerAdvice, cette classe centralise la gestion des erreurs pour tous les contrôleurs de l'application (au lieu de faire un try/catch dans chaque méthode).

/**
 * Gestionnaire global des exceptions pour les contrôleurs de l'application.
 * <p>
 * Centralise la conversion des exceptions métier et de validation en réponses HTTP.
 */
@ControllerAdvice
public class VilleExceptionHandler {

    // @ExceptionHandler(VilleException.class), dès qu'une VilleException est levée n'importe où dans un contrôleur, Spring appelle automatiquement cette méthode pour construire la réponse.
    /**
     * Intercepte les {@link VilleException} et les convertit en réponse HTTP 400.
     *
     * @param exception exception métier levée
     * @return une réponse 400 (Bad Request) portant le message de l'exception
     */
    @ExceptionHandler(VilleException.class)
    public ResponseEntity<String> handleVilleException(VilleException exception) {
        // Je renvoie le message de l'exception avec un code 400 (Bad Request), car ce sont des erreurs liées à des données invalides envoyées par le client.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
    // @ExceptionHandler(MethodArgumentNotValidException.class), cette exception est levée automatiquement par Spring
    // quand @Valid détecte qu'un contrôle Bean Validation (@NotNull, @Size, @Min...) n'est pas respecté.
    /**
     * Intercepte les erreurs de Bean Validation (déclenchées par {@code @Valid}) et les convertit en réponse HTTP 400.
     *
     * @param exception exception de validation levée par Spring
     * @return une réponse 400 (Bad Request) indiquant le champ en erreur et le message associé
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
        // Je récupère la première erreur de validation trouvée (il peut y en avoir plusieurs si plusieurs champs sont invalides)
        FieldError erreur = exception.getBindingResult().getFieldErrors().getFirst();
        // Je construis un message clair, le nom du champ en erreur + le message associé
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erreur.getField() + " : " + erreur.getDefaultMessage());
    }
}