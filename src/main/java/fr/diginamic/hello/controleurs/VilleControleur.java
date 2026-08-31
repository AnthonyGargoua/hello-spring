package fr.diginamic.hello.controleurs;


import fr.diginamic.hello.entities.Ville;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ville")
public class VilleControleur {

    @GetMapping
    public List<Ville> getVilles (){
        List<Ville> villes = new ArrayList<>();
        villes.add(new Ville("Paris", 786490.0));
        villes.add(new Ville("Montpellier", 3465875.0));
        villes.add(new Ville("Bordeaux", 546780.0));
        villes.add(new Ville("Toulouse", 2345678.0));
        villes.add(new Ville("Annecy", 3456789.0));
        return villes;
    }
}
