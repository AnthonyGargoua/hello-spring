package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.exceptions.VilleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Interface exposant la documentation Swagger des opérations CRUD et de recherche sur les villes.
 * <p>
 * Les annotations Swagger et de mapping vivent ici, VilleControleur ne fait qu'implémenter la logique.
 */
public interface VilleControleursDocs {

    /**
     * Récupère la liste de toutes les villes.
     *
     * @return la liste de toutes les villes
     */
    @Operation(summary = "Récupérer la liste de toutes les villes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes renvoyée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class))))
    })
    @GetMapping
    List<VilleDto> getVilles();

    // @PathVariable, récupère la valeur présente dans l'URL (ex: /ville/3 -> id = 3)
    /**
     * Récupère une ville à partir de son identifiant.
     *
     * @param id identifiant de la ville recherchée
     * @return la ville trouvée, ou une réponse 404 si aucune ville ne correspond
     */
    @Operation(summary = "Récupérer une ville par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville trouvée",
                    content = @Content(schema = @Schema(implementation = VilleDto.class))),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id")
    })
    @GetMapping("/{id}")
    ResponseEntity<VilleDto> getVilleParId(
            @Parameter(description = "Identifiant de la ville", example = "1", required = true)
            @PathVariable int id);

    // Ici le VilleExceptionHandler permet d'intercepter l'exception automatiquement
    // @Valid déclenche automatiquement le contrôle des annotations posées sur VilleDto (@NotNull, @Size, @Min...)
    // Si un contrôle échoue, Spring lève lui-même une MethodArgumentNotValidException, qu'on va récupérer dans le ControllerAdvice
    /**
     * Ajoute une nouvelle ville.
     *
     * @param nouvelleVilleDto ville à créer
     * @return un message de confirmation
     * @throws VilleException si la ville est invalide ou existe déjà
     */
    @Operation(summary = "Ajouter une nouvelle ville")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville insérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou déjà existante")
    })
    @PostMapping
    ResponseEntity<String> insertVille(@Valid @RequestBody VilleDto nouvelleVilleDto) throws VilleException;

    // @RequestBody, Spring convertit automatiquement le JSON envoyé dans le corps de la requête en objet VilleDto, avec les nouvelles valeurs à appliquer
    // Même contrôle Bean Validation que sur le POST : @Valid ici aussi déclenche la vérification, et c'est le même ExceptionHandler (défini une seule fois)
    // qui va intercepter l'erreur
    /**
     * Modifie une ville existante.
     *
     * @param id identifiant de la ville à modifier
     * @param villeModifieeDto ville portant les nouvelles données
     * @return un message de confirmation
     * @throws VilleException si aucune ville ne correspond à l'id ou si les données sont invalides
     */
    @Operation(summary = "Modifier une ville existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    ResponseEntity<String> updateVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true)
            @PathVariable int id,
            @Valid @RequestBody VilleDto villeModifieeDto) throws VilleException;

    // Même logique de recherche par id que pour le GET et le PUT, mais ici on supprime la ville trouvée au lieu de la lire ou la modifier.
    /**
     * Supprime une ville à partir de son identifiant.
     *
     * @param id identifiant de la ville à supprimer
     * @return un message de confirmation
     * @throws VilleException si aucune ville ne correspond à l'id
     */
    @Operation(summary = "Supprimer une ville par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<String> removeVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true)
            @PathVariable int id) throws VilleException;

    // @RequestParam permet de récupérer une valeur dans les paramètres de l'url après le ?
    // Par exemple : /ville/recherche/nom?nom=Bor -> nom = "Bor"
    /**
     * Recherche les villes dont le nom commence par une chaîne donnée.
     *
     * @param nom début du nom recherché
     * @return la liste des villes correspondantes
     * @throws VilleException si aucune ville ne correspond
     */
    @Operation(summary = "Rechercher des villes dont le nom commence par une chaîne donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/nom")
    ResponseEntity<List<VilleDto>> rechercherParNom(
            @Parameter(description = "Début du nom recherché", example = "Pa", required = true)
            @RequestParam String nom) throws VilleException;

    // Même principe que rechercherParNom, mais on filtre sur la population plutôt que sur le nom
    /**
     * Recherche les villes dont la population dépasse un minimum donné.
     *
     * @param min population minimale
     * @return la liste des villes correspondantes
     * @throws VilleException si aucune ville ne correspond
     */
    @Operation(summary = "Rechercher les villes dont la population dépasse un minimum")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/population-min")
    ResponseEntity<List<VilleDto>> rechercherParPopulationMin(
            @Parameter(description = "Population minimale", example = "100000", required = true)
            @RequestParam Integer min) throws VilleException;

    // Même logique, mais avec deux paramètres (min et max) pour filtrer une fourchette de population
    /**
     * Recherche les villes dont la population est comprise entre deux valeurs données.
     *
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes correspondantes
     * @throws VilleException si aucune ville ne correspond
     */
    @Operation(summary = "Rechercher les villes dont la population est comprise entre deux valeurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/population-min-max")
    ResponseEntity<List<VilleDto>> rechercherParPopulationMinMax(
            @Parameter(description = "Population minimale", example = "100000", required = true)
            @RequestParam Integer min,
            @Parameter(description = "Population maximale", example = "1000000", required = true)
            @RequestParam Integer max) throws VilleException;

    // @PathVariable pour idDepartement et n (ils font partie de l'URL), @RequestParam pour min/max (après le ?), comme sur les autres recherches
    /**
     * Recherche les n villes les plus peuplées d'un département donné.
     *
     * @param idDepartement identifiant du département
     * @param n nombre de villes à renvoyer
     * @return la liste des n villes les plus peuplées du département, triées par population décroissante
     * @throws VilleException si aucune ville ne correspond
     */
    @Operation(summary = "Rechercher les n villes les plus peuplées d'un département")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/departement/{idDepartement}/top/{n}")
    ResponseEntity<List<VilleDto>> rechercherTopNParDepartement(
            @Parameter(description = "Identifiant du département", example = "1", required = true)
            @PathVariable int idDepartement,
            @Parameter(description = "Nombre de villes à renvoyer", example = "5", required = true)
            @PathVariable int n) throws VilleException;

    /**
     * Recherche les villes d'un département dont la population est comprise entre deux valeurs données.
     *
     * @param idDepartement identifiant du département
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes correspondantes
     * @throws VilleException si aucune ville ne correspond
     */
    @Operation(summary = "Rechercher les villes d'un département dont la population est comprise entre deux valeurs")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes correspondantes",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VilleDto.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/departement/{idDepartement}/population")
    ResponseEntity<List<VilleDto>> rechercherParDepartementEtPopulationMinMax(
            @Parameter(description = "Identifiant du département", example = "1", required = true)
            @PathVariable int idDepartement,
            @Parameter(description = "Population minimale", example = "10000", required = true)
            @RequestParam Integer min,
            @Parameter(description = "Population maximale", example = "1000000", required = true)
            @RequestParam Integer max) throws VilleException;
}