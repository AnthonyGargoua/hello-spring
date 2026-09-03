package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.DepartementDto;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.mappers.DepartementMapper;
import fr.diginamic.hello.services.DepartementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur REST exposant les opérations CRUD sur les départements.
 * <p>
 * La documentation Swagger est déclarée dans {@link DepartementControleursDocs}.
 */
@RestController
@RequestMapping("/departement")
public class DepartementControleur implements DepartementControleursDocs {

    private final DepartementService departementService;
    private final DepartementMapper departementMapper;

    /**
     * Crée le contrôleur en lui injectant le service métier des départements et le mapper.
     *
     * @param departementService service utilisé pour gérer les départements
     * @param departementMapper mapper utilisé pour convertir entre Departement et DepartementDto
     */
    public DepartementControleur(DepartementService departementService, DepartementMapper departementMapper){
        this.departementService = departementService;
        this.departementMapper = departementMapper;
    }

    @Override
    public List<DepartementDto> getDepartements(){
        // Ici, je récupère tous les Departement du service, puis je convertis chacun en DepartementDto avec le stream
        List<Departement> departements = departementService.extractDepartements();
        return departements.stream().map(departementMapper::toDto).toList();
    }

    @Override
    public ResponseEntity<DepartementDto> getDepartementParId(int id){
        Departement departement = departementService.extractDepartement(id);

        if(departement == null){
            return ResponseEntity.notFound().build();
        }

        // Ici, je convertis le Departement trouvé en DepartementDto avant de le renvoyer
        return ResponseEntity.ok(departementMapper.toDto(departement));
    }

    @Override
    public ResponseEntity<String> insertDepartement(DepartementDto nouveauDepartementDto) throws VilleException{
        // Ici, je convertis le DepartementDto reçu en Departement avant de le passer au service
        Departement nouveauDepartement = departementMapper.toBean(nouveauDepartementDto);
        departementService.insertDepartement(nouveauDepartement);
        return ResponseEntity.ok("Département inséré avec succès");
    }

    @Override
    public ResponseEntity<String> updateDepartement(int id, DepartementDto departementModifieDto) throws VilleException{
        Departement departementModifie = departementMapper.toBean(departementModifieDto);
        departementService.updateDepartement(id, departementModifie);
        return ResponseEntity.ok("Département modifié avec succès");
    }

    @Override
    public ResponseEntity<String> removeDepartement(int id) throws VilleException{
        departementService.removeDepartement(id);
        return ResponseEntity.ok("Département supprimé avec succès");
    }
}