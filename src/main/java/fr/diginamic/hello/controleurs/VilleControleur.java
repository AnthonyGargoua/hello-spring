package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.mappers.VilleMapper;
import fr.diginamic.hello.services.VilleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Crée le contrôleur en lui injectant le service métier des villes et le mapper.
     *
     * @param villeService service utilisé pour gérer les villes
     * @param villeMappers mapper utilisé pour convertir entre Ville et VilleDto
     */
    public VilleControleur(VilleService villeService, VilleMapper villeMappers){
        this.villeService = villeService;
        this.villeMappers = villeMappers;
    }

    @Override
    public List<VilleDto> getVilles(){
        // Ici, je récupère toutes les Ville du service, puis je convertis chacune en VilleDto avec le stream
        List<Ville> villes = villeService.extractVilles();
        return villes.stream().map(villeMappers::toDto).toList();
    }

    @Override
    public ResponseEntity<VilleDto> getVilleParId(int id){
        Ville ville = villeService.extractVille(id);

        if(ville == null){
            return ResponseEntity.notFound().build();
        }

        // Ici, je convertis la Ville trouvée en VilleDto avant de la renvoyer
        return ResponseEntity.ok(villeMappers.toDto(ville));
    }

    @Override
    public ResponseEntity<String> insertVille(VilleDto nouvelleVilleDto) throws VilleException{
        // Ici, je convertis le VilleDto reçu en Ville avant de le passer au service, qui continue de travailler avec l'entité
        Ville nouvelleVille = villeMappers.toBean(nouvelleVilleDto);
        // Ici, je transmets aussi le code et l'id département du DTO, pour que le service puisse résoudre (ou créer) le bon département
        villeService.insertVille(nouvelleVille, nouvelleVilleDto.getCodeDepartement(), nouvelleVilleDto.getIdDepartement());
        return ResponseEntity.ok("Ville insérée avec succès");
    }

    @Override
    public ResponseEntity<String> updateVille(int id, VilleDto villeModifieeDto) throws VilleException{
        Ville villeModifiee = villeMappers.toBean(villeModifieeDto);
        villeService.updateVille(id, villeModifiee);
        return ResponseEntity.ok("Ville modifiée avec succès");
    }

    @Override
    public ResponseEntity<String> removeVille(int id) throws VilleException{
        villeService.removeVille(id);
        return ResponseEntity.ok("Ville supprimée avec succès");
    }

    @Override
    public ResponseEntity<List<VilleDto>> rechercherParNom(String nom) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(nom);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville dont le nom commence par " + nom + " n'a été trouvée");
        }
        // Ici aussi, je convertis la liste de Ville en liste de VilleDto avec le stream avant de la renvoyer
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    public ResponseEntity<List<VilleDto>> rechercherParPopulationMin(Integer min) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(min);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population supérieure à " + min);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    public ResponseEntity<List<VilleDto>> rechercherParPopulationMinMax(Integer min, Integer max) throws VilleException{
        List<Ville> resultat = villeService.extractVilles(min, max);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    public ResponseEntity<List<VilleDto>> rechercherTopNParDepartement(int idDepartement, int n) throws VilleException{
        List<Ville> resultat = villeService.extractTopNVillesParDepartement(idDepartement, n);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville trouvée pour le département " + idDepartement);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }

    @Override
    public ResponseEntity<List<VilleDto>> rechercherParDepartementEtPopulationMinMax(int idDepartement, Integer min, Integer max) throws VilleException{
        List<Ville> resultat = villeService.extractVillesParDepartementEtMinMax(idDepartement, min, max);

        if(resultat.isEmpty()){
            throw new VilleException("Aucune ville n'a une population comprise entre " + min + " et " + max + " dans le département " + idDepartement);
        }
        return ResponseEntity.ok(resultat.stream().map(villeMappers::toDto).toList());
    }
}