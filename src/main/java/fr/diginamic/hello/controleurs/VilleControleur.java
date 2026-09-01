package fr.diginamic.hello.controleurs;


import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ville")
public class VilleControleur {

    private List<Ville> villes = new ArrayList<>();

    // J'initialise un compteur pour l'id
    private int prochainId = 1;

    // @PostConstruct, cette méthode est exécutée automatiquement par Spring Boot, une seule fois, juste après la création du contrôleur (au démarrage de l'appli)
    // Ça me sert à simuler des données déjà présentes en base
    @PostConstruct
    public void initData(){
        ajouterEnListe(new Ville("Paris", 786490.0));
        ajouterEnListe(new Ville("Montpellier", 3465875.0));
        ajouterEnListe(new Ville("Bordeaux", 546780.0));
        ajouterEnListe(new Ville("Toulouse", 2348585.0));
        ajouterEnListe(new Ville("Annecy", 3456789.0));
    }

    // J'attribue un id unique et j'ajoute la nouvelle ville à la liste
    private void ajouterEnListe(Ville ville){
        ville.setId(prochainId);
        prochainId++;
        villes.add(ville);
    }

    // Je recherche une ville par son id. Si aucune valeur (renvoie null, si une valeur renvoie la valeur)
    // stream/filter/findFirst, je parcours la liste, je garde la ville dont l'id correspond, et je récupère la première trouvée (ou null s'il n'y en a pas)
    private Ville trouverParId(int id){
        return villes.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }

    // Je regroupe les controles métier communs à l'ajout et la modification
    // VilleException est déjà checker, je déclare la méthode Throws VilleException
    private void validerVille (Ville ville) throws VilleException{
        if(ville.getPopulation() == null || ville.getPopulation() < 10){
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
        if(ville.getNom() == null || ville.getNom().length() < 2){
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
    }

    @Operation(summary = "Récupérer la liste de toutes les villes")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des villes renvoyée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Ville.class))))
    })
    @GetMapping
    public List<Ville> getVilles(){
        return villes;
    }

    // @PathVariable, récupère la valeur présente dans l'URL (ex: /ville/3 -> id = 3)
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
        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ville);
    }

    // Ici le VilleExceptionHandler permet d'intercepter l'exception automatiquement
    @Operation(summary = "Ajouter une nouvelle ville")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville insérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Ville invalide ou déjà existante")
    })
    @PostMapping
    public ResponseEntity<String> ajouterVille(@RequestBody Ville nouvelleVille) throws VilleException{
        validerVille(nouvelleVille);

        boolean existeDeja = villes.stream().anyMatch(v -> v.getNom().equals(nouvelleVille.getNom()));

        if(existeDeja){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
        }

        ajouterEnListe(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    // @RequestBody, Spring convertit automatiquement le JSON envoyé dans le corps de la requête en objet Ville, avec les nouvelles valeurs à appliquer
    @Operation(summary = "Modifier une ville existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville modifiée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> modifierVille(
            @Parameter(description = "Identifiant de la ville à modifier", example = "1", required = true)
            @PathVariable int id,
            @RequestBody Ville villeModifiee) throws VilleException{
        validerVille(villeModifiee);

        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune ville trouvée par l'id " + id);
        }

        ville.setNom(villeModifiee.getNom());
        ville.setPopulation(villeModifiee.getPopulation());

        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    // Même logique de recherche par id que pour le GET et le PUT (trouverParId), mais ici on supprime la ville trouvée de la liste au lieu de la lire ou la modifier.
    @Operation(summary = "Supprimer une ville par son id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ville supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Aucune ville trouvée pour cet id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerVille(
            @Parameter(description = "Identifiant de la ville à supprimer", example = "1", required = true)
            @PathVariable int id){
        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune ville trouvée par l'id " + id);
        }

        villes.remove(ville);
        return ResponseEntity.ok("Ville supprimée avec succès");
    }

    // @RequestParam permet de récupérer une valeur dans les paramètres de l'url après le ?
    // Par exemple : /ville/recherche/nom?nom=Bor -> nom = "Bor"
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
        // .collect(Collectors.toList()) transforme le flux filtré en une vraie List, alors que findFirst() qui ne renvoyait qu'un seul élément.
        List<Ville> resultat = villes.stream().filter(v -> v.getNom().startsWith(nom)).collect(Collectors.toList());

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville dont le nom commence par " + nom + " n'a été trouvée");
        }
        return ResponseEntity.ok(resultat);
    }

    // Même principe que rechercherParNom, mais on filtre sur la population plutôt que sur le nom
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
        List<Ville> resultat = villes.stream().filter(v -> v.getPopulation() > min).collect(Collectors.toList());

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return ResponseEntity.ok(resultat);
    }

    // Même logique, mais avec deux paramètres (min et max) pour filtrer une fourchette de population
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
        List<Ville> resultat = villes.stream().filter(v -> v.getPopulation() >= min && v.getPopulation() <= max).collect(Collectors.toList());

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return ResponseEntity.ok(resultat);
    }
}