package fr.diginamic.hello.controleurs;


import fr.diginamic.hello.entities.Ville;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ville")
public class VilleControleur {

    private List<Ville> villes = new ArrayList<>();

    // J'initialise un compteur pour l'id
    private int prochainId = 1;

    // @PostConstruct : cette méthode est exécutée automatiquement par Spring Boot, une seule fois, juste après la création du contrôleur (au démarrage de l'appli)
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
    // stream/filter/findFirst : je parcours la liste, je garde la ville dont l'id correspond, et je récupère la première trouvée (ou null s'il n'y en a pas)
    private Ville trouverParId(int id){
        return villes.stream().filter(v -> v.getId() == id).findFirst().orElse(null);
    }

    @GetMapping
    public List<Ville> getVilles(){
        return villes;
    }

    // @PathVariable : récupère la valeur présente dans l'URL (ex: /ville/3 -> id = 3)
    @GetMapping("/{id}")
    public ResponseEntity<Ville> getVilleParId(@PathVariable int id){
        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ville);
    }

    @PostMapping
    public ResponseEntity<String> ajouterVille(@RequestBody Ville nouvelleVille){
        boolean existeDeja = villes.stream().anyMatch(v -> v.getNom().equals(nouvelleVille.getNom()));

        if(existeDeja){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
        }

        ajouterEnListe(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    // @RequestBody : Spring convertit automatiquement le JSON envoyé dans le corps de la requête en objet Ville, avec les nouvelles valeurs à appliquer
    @PutMapping("/{id}")
    public ResponseEntity<String> modifierVille(@PathVariable int id, @RequestBody Ville villeModifiee){
        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune ville trouvée par l'id " + id);
        }

        ville.setNom(villeModifiee.getNom());
        ville.setPopulation(villeModifiee.getPopulation());

        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerVille(@PathVariable int id){
        Ville ville = trouverParId(id);

        if(ville == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aucune ville trouvée par l'id " + id);
        }

        villes.remove(ville);
        return ResponseEntity.ok("Ville supprimée avec succès");
    }
}