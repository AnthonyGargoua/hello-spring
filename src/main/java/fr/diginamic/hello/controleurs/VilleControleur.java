package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.export.VilleCsvExporter;
import fr.diginamic.hello.mappers.VilleMapper;
import fr.diginamic.hello.services.VilleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD et de recherche sur les villes.
 * <p>
 * La documentation Swagger est déclarée dans {@link VilleControleursDocs}.
 */
@RestController
@RequestMapping("/ville")
public class VilleControleur implements VilleControleursDocs {

    private final VilleService villeService;
    private final VilleMapper villeMappers;
    private final VilleCsvExporter villeCsvExporter;

    /**
     * Crée le contrôleur en lui injectant le service métier des villes, le mapper et l'exporteur CSV.
     *
     * @param villeService service utilisé pour gérer les villes
     * @param villeMappers mapper utilisé pour convertir entre Ville et VilleDto
     * @param villeCsvExporter composant utilisé pour générer le fichier CSV d'export
     */
    public VilleControleur(VilleService villeService, VilleMapper villeMappers, VilleCsvExporter villeCsvExporter){
        this.villeService = villeService;
        this.villeMappers = villeMappers;
        this.villeCsvExporter = villeCsvExporter;
    }

    @Override
    @GetMapping
    public Page<VilleDto> getVilles(@RequestParam(defaultValue="0")int page, @RequestParam(defaultValue = "20") int size){
        // Ici, je récupère toutes les Ville du service, puis je convertis chacune en VilleDto avec le stream
        Page<Ville> villes = villeService.extractVillesPaginees(page, size);
        return villes.map(villeMappers::toDto);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<VilleDto> getVilleParId(@PathVariable int id){
        Ville ville = villeService.extractVille(id);

        if(ville == null){
            return ResponseEntity.notFound().build();
        }

        // Ici, je convertis la Ville trouvée en VilleDto avant de la renvoyer
        return ResponseEntity.ok(villeMappers.toDto(ville));
    }

    @Override
    @GetMapping("/departement/{idDepartement}/population-min")
    public ResponseEntity<List<VilleDto>> rechercherParDepartementEtPopulationMin(@PathVariable int idDepartement, @RequestParam Integer min) throws VilleException{
        List<Ville> resultat = villeService.extractVillesParDepartementEtMin(idDepartement, min);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieur à " + min + " dans le département " + idDepartement);
        }
        // Ici, je convertis la Ville trouvée en VilleDto avant de la renvoyer
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    @PostMapping
    public ResponseEntity<String> insertVille(@Valid @RequestBody VilleDto nouvelleVilleDto) throws VilleException{
        // Ici, je convertis le VilleDto reçu en Ville avant de le passer au service, qui continue de travailler avec l'entité
        Ville nouvelleVille = villeMappers.toBean(nouvelleVilleDto);
        // Ici, je transmets aussi le code et l'id département du DTO, pour que le service puisse résoudre (ou créer) le bon département
        villeService.insertVille(nouvelleVille, nouvelleVilleDto.getCodeDepartement(), nouvelleVilleDto.getIdDepartement());
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVille(@PathVariable int id, @Valid @RequestBody VilleDto villeModifieeDto) throws VilleException{
        Ville villeModifiee = villeMappers.toBean(villeModifieeDto);
        villeService.updateVille(id, villeModifiee);
        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeVille(@PathVariable int id) throws VilleException{
        villeService.removeVille(id);
        return ResponseEntity.ok("Ville supprimée avec succès");
    }

    @Override
    @GetMapping("/recherche/nom/{nom}")
    public ResponseEntity<VilleDto> rechercherParNom(@PathVariable String nom) throws VilleException{
        Ville resultat = villeService.extractVilleParNom(nom);

        if(resultat == null){
            throw new VilleException("Aucune ville portant le nom " + nom + " n'a été trouvée");
        }
        // Ici, je convertis la Ville trouvée en VilleDto avant de la renvoyer
        return ResponseEntity.ok(villeMappers.toDto(resultat));
    }

    @Override
    @GetMapping("/recherche/population-min/{min}")
    public ResponseEntity<List<VilleDto>> rechercherParPopulationMin(@PathVariable Integer min) throws VilleException{
        List<Ville> resultat = villeService.extractVillesParPopulationMin(min);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    @GetMapping("/recherche/population-min-max/{min}/{max}")
    public ResponseEntity<List<VilleDto>> rechercherParPopulationMinMax(@PathVariable Integer min, @PathVariable Integer max) throws VilleException{
        List<Ville> resultat = villeService.extractVillesParPopulationMinMax(min, max);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    @GetMapping("/departement/{idDepartement}/top/{n}")
    public ResponseEntity<List<VilleDto>> rechercherTopNParDepartement(@PathVariable int idDepartement, @PathVariable int n) throws VilleException{
        List<Ville> resultat = villeService.extractTopNVillesParDepartement(idDepartement, n);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville trouvée pour le département " + idDepartement);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    @GetMapping("/departement/{idDepartement}/population")
    public ResponseEntity<List<VilleDto>> rechercherParDepartementEtPopulationMinMax(@PathVariable int idDepartement, @RequestParam Integer min, @RequestParam Integer max) throws VilleException{
        List<Ville> resultat = villeService.extractVillesParDepartementEtMinMax(idDepartement, min, max);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max + " dans le département " + idDepartement);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exporterVillesCsv(@RequestParam Integer min) throws VilleException{
        List<Ville> villes = villeService.extractVillesParPopulationMin(min);

        if(villes.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }

        byte[] contenu = villeCsvExporter.genererCsv(villes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("villes_population_min_" + min + ".csv", StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok().headers(headers).body(contenu);
    }
}
