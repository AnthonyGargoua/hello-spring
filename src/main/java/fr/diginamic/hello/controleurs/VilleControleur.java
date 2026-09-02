package fr.diginamic.hello.controleurs;


import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.services.VilleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid; // permet d'utiliser @Valid, l'annotation qui déclenche le contrôle Bean Validation
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD et de recherche sur les villes.
 */
@RestController
@RequestMapping("/ville")
public class VilleControleur {

private final VilleService villeService;

/**
 * Crée le contrôleur en lui injectant le service métier des villes.
 *
 * @param villeService service utilisé pour gérer les villes
 */
public VilleControleur(VilleService villeService){
    this.villeService = villeService;
}

    /**
     * Récupère la liste de toutes les villes.
     *
     * @return la liste de toutes les villes
     */
    @Operation(summary = "Récupérer la liste de toutes les villes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes renvoyée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ville.class))))
    })
    @GetMapping
    public List<Ville> getVilles(){
        return villeService.extractVilles();
    }

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
                    content = @Content(schema = @Schema(implementation = Ville.class))),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Ville> getVilleParId(
            @Parameter(description = "Identifiant de la ville", example = "1", required = true)
            @PathVariable int id){
        Ville ville = villeService.extractVille(id);

        if(ville == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ville);
    }

    // Ici le VilleExceptionHandler permet d'intercepter l'exception automatiquement
    /**
     * Ajoute une nouvelle ville.
     *
     * @param nouvelleVille ville à créer
     * @return un message de confirmation
     * @throws VilleException si la ville est invalide ou existe déjà
     */
    @Operation(summary = "Ajouter une nouvelle ville")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville insérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou déjà existante")
    })
    // @Valid déclenche automatiquement le contrôle des annotations posées sur Ville (@NotNull, @Size, @Min...)
    // Si un contrôle échoue, Spring lève lui-même une MethodArgumentNotValidException, qu'on va récupérer dans le ControllerAdvice
    @PostMapping
    public ResponseEntity<String> insertVille(@Valid @RequestBody Ville nouvelleVille) throws VilleException{
        villeService.insertVille(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    // @RequestBody, Spring convertit automatiquement le JSON envoyé dans le corps de la requête en objet Ville, avec les nouvelles valeurs à appliquer
    /**
     * Modifie une ville existante.
     *
     * @param id identifiant de la ville à modifier
     * @param villeModifiee ville portant les nouvelles données
     * @return un message de confirmation
     * @throws VilleException si aucune ville ne correspond à l'id ou si les données sont invalides
     */
    @Operation(summary = "Modifier une ville existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    // Même contrôle Bean Validation que sur le POST : @Valid ici aussi déclenche la vérification, et c'est le même ExceptionHandler (défini une seule fois)
    // qui va intercepter l'erreur
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true)
            @PathVariable int id,
            @Valid @RequestBody Ville villeModifiee) throws VilleException{
        villeService.updateVille(id, villeModifiee);
        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    // Même logique de recherche par id que pour le GET et le PUT (trouverParId), mais ici on supprime la ville trouvée de la liste au lieu de la lire ou la modifier.
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
    public ResponseEntity<String> removeVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true)
            @PathVariable int id) throws VilleException{
        villeService.removeVille(id);
        return ResponseEntity.ok("Ville supprimée avec succès");
    }

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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ville.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/nom")
    public ResponseEntity<List<Ville>> rechercherParNom(
            @Parameter(description = "Début du nom recherché", example = "Pa", required = true)
            @RequestParam String nom) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(nom);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville dont le nom commence par " + nom + " n'a été trouvée");
        }
        return ResponseEntity.ok(resultat);
    }

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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ville.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/population-min")
    public ResponseEntity<List<Ville>> rechercherParPopulationMin(
            @Parameter(description = "Population minimale", example = "100000", required = true)
            @RequestParam Integer min) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(min);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return ResponseEntity.ok(resultat);
    }

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
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ville.class)))),
            @ApiResponse(responseCode = "400", description = "Aucune ville trouvée")
    })
    @GetMapping("/recherche/population-min-max")
    public ResponseEntity<List<Ville>> rechercherParPopulationMinMax(
            @Parameter(description = "Population minimale", example = "100000", required = true)
            @RequestParam Integer min,
            @Parameter(description = "Population maximale", example = "1000000", required = true)
            @RequestParam Integer max) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(min, max);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return ResponseEntity.ok(resultat);
    }
}