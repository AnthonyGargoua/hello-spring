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

    @PostConstruct
    public void initData(){
        villes.add(new Ville("Paris", 786490.0));
        villes.add(new Ville("Montpellier", 3465875.0));
        villes.add(new Ville("Bordeaux", 546780.0));
        villes.add(new Ville("Toulouse", 2348585.0));
        villes.add(new Ville("Annecy", 3456789.0));
    }

    @GetMapping
    public List<Ville> getVilles(){
        return villes;
    }

    @PostMapping
    public ResponseEntity<String> ajouterVille(@RequestBody Ville nouvelleVille){
        boolean existeDeja = villes.stream().anyMatch(v -> v.getNom().equals(nouvelleVille.getNom()));

        if(existeDeja){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La ville existe déjà");
        }

        villes.add(nouvelleVille);
        return ResponseEntity.ok("Ville insérée avec succès");
    }
}
