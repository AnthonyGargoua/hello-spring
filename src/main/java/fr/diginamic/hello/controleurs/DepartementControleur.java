package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.DepartementDto;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.export.DepartementPdfExporter;
import fr.diginamic.hello.mappers.DepartementMapper;
import fr.diginamic.hello.services.DepartementService;
import fr.diginamic.hello.services.VilleService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final VilleService villeService;
    private final DepartementPdfExporter departementPdfExporter;

    /**
     * Crée le contrôleur en lui injectant le service métier des départements, le mapper,
     * le service des villes et l'exporteur PDF.
     *
     * @param departementService service utilisé pour gérer les départements
     * @param departementMapper mapper utilisé pour convertir entre Departement et DepartementDto
     * @param villeService service utilisé pour récupérer les villes d'un département lors de l'export PDF
     * @param departementPdfExporter composant utilisé pour générer le fichier PDF d'export
     */
    public DepartementControleur(DepartementService departementService, DepartementMapper departementMapper,
                                  VilleService villeService, DepartementPdfExporter departementPdfExporter){
        this.departementService = departementService;
        this.departementMapper = departementMapper;
        this.villeService = villeService;
        this.departementPdfExporter = departementPdfExporter;
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

    @Override
    public ResponseEntity<byte[]> exporterDepartementPdf(String code) throws VilleException{
        Departement departement = departementService.extractDepartementParCode(code);

        if(departement == null){
            throw new VilleException("Aucun département trouvé pour le code " + code);
        }

        List<Ville> villes = villeService.extractVillesParDepartement(departement);
        byte[] contenu = departementPdfExporter.genererPdf(departement, villes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("departement_" + departement.getCode() + ".pdf")
                .build());

        return ResponseEntity.ok().headers(headers).body(contenu);
    }
}
