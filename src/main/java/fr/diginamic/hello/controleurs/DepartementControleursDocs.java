package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.DepartementDto;
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
 * Interface exposant la documentation Swagger des opérations CRUD sur les départements.
 * <p>
 * Les annotations Swagger et de mapping vivent ici, DepartementControleur ne fait qu'implémenter la logique.
 */
public interface DepartementControleursDocs {

    /**
     * Récupère la liste de tous les départements.
     *
     * @return la liste de tous les départements
     */
    @Operation(summary = "Récupérer la liste de tous les départements")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des départements renvoyée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DepartementDto.class))))
    })
    @GetMapping
    List<DepartementDto> getDepartements();

    // @PathVariable, récupère la valeur présente dans l'URL (ex: /departement/3 -> id = 3)
    /**
     * Récupère un département à partir de son identifiant.
     *
     * @param id identifiant du département recherché
     * @return le département trouvé, ou une réponse 404 si aucun département ne correspond
     */
    @Operation(summary = "Récupérer un département par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département trouvé",
                    content = @Content(schema = @Schema(implementation = DepartementDto.class))),
            @ApiResponse(responseCode = "404", description = "Aucun département trouvé pour cet id")
    })
    @GetMapping("/{id}")
    ResponseEntity<DepartementDto> getDepartementParId(
            @Parameter(description = "Identifiant du département", example = "1", required = true)
            @PathVariable int id);

    // Ici le VilleExceptionHandler permet d'intercepter l'exception automatiquement (il fonctionne pour tous les contrôleurs, pas que Ville)
    // @Valid déclenche automatiquement le contrôle des annotations posées sur DepartementDto (@NotNull, @Size...)
    /**
     * Ajoute un nouveau département.
     *
     * @param nouveauDepartementDto département à créer
     * @return un message de confirmation
     * @throws VilleException si le département est invalide
     */
    @Operation(summary = "Ajouter un nouveau département")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département inséré avec succès"),
            @ApiResponse(responseCode = "400", description = "Département invalide")
    })
    @PostMapping
    ResponseEntity<String> insertDepartement(@Valid @RequestBody DepartementDto nouveauDepartementDto) throws VilleException;

    // @RequestBody, Spring convertit automatiquement le JSON envoyé dans le corps de la requête en objet DepartementDto
    /**
     * Modifie un département existant.
     *
     * @param id identifiant du département à modifier
     * @param departementModifieDto département portant les nouvelles données
     * @return un message de confirmation
     * @throws VilleException si aucun département ne correspond à l'id ou si les données sont invalides
     */
    @Operation(summary = "Modifier un département existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun département trouvé pour cet id"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    ResponseEntity<String> updateDepartement(
            @Parameter(description = "Identifiant du département à modifier", example = "1", required = true)
            @PathVariable int id,
            @Valid @RequestBody DepartementDto departementModifieDto) throws VilleException;

    // Même logique de recherche par id que pour le GET et le PUT, mais ici on supprime le département trouvé au lieu de le lire ou le modifier.
    /**
     * Supprime un département à partir de son identifiant.
     *
     * @param id identifiant du département à supprimer
     * @return un message de confirmation
     * @throws VilleException si aucun département ne correspond à l'id
     */
    @Operation(summary = "Supprimer un département par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Département supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucun département trouvé pour cet id")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<String> removeDepartement(
            @Parameter(description = "Identifiant du département à supprimer", example = "1", required = true)
            @PathVariable int id) throws VilleException;

    /**
     * Exporte en PDF la fiche d'un département (code, nom et villes rattachées).
     *
     * @param code code du département à exporter
     * @return le fichier PDF correspondant, à télécharger
     * @throws VilleException si aucun département ne correspond au code
     */
    @Operation(summary = "Exporter en PDF la fiche d'un département et de ses villes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fichier PDF généré avec succès",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "400", description = "Aucun département trouvé pour ce code")
    })
    @GetMapping(value = "/{code}/export", produces = "application/pdf")
    ResponseEntity<byte[]> exporterDepartementPdf(
            @Parameter(description = "Code du département", example = "75", required = true)
            @PathVariable String code) throws VilleException;
}