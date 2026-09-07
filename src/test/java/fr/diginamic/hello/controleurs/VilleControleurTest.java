package fr.diginamic.hello.controleurs;

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.export.VilleCsvExporter;
import fr.diginamic.hello.mappers.VilleMapper;
import fr.diginamic.hello.services.VilleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test de la couche web de {@link VilleControleur}, avec MockMvc et Mockito.
 * <p>
 * {@code @WebMvcTest} ne charge que le contrôleur et l'infrastructure MVC (pas de vraie base,
 * pas de vrai service) : {@link VilleService}, {@link VilleMapper} et {@link VilleCsvExporter}
 * sont remplacés par des mocks ({@code @MockitoBean}) car ce sont les 3 dépendances du
 * constructeur de {@link VilleControleur}. {@link MockMvc} simule des requêtes HTTP sans
 * démarrer de vrai serveur.
 */
@WebMvcTest(VilleControleur.class)
public class VilleControleurTest {

    /** Simule les requêtes HTTP vers le contrôleur, sans vrai serveur Tomcat. */
    @Autowired
    private MockMvc mockMvc;

    /** Faux service : on définit ici ce qu'il doit renvoyer pour chaque test. */
    @MockitoBean
    private VilleService villeService;

    /** Faux mapper : on définit aussi ses conversions Ville <-> VilleDto pour chaque test. */
    @MockitoBean
    private VilleMapper villeMappers;

    /** Faux exporteur CSV : non utilisé dans nos tests, mais requis pour construire le contrôleur. */
    @MockitoBean
    private VilleCsvExporter villeCsvExporter;

    /**
     * Vérifie que GET /ville/{id} renvoie bien la ville au format JSON attendu.
     */
    @Test
    void getVilleParId_devraitRenvoyerLaVille_siIdExiste() throws Exception {
        Ville ville = new Ville();
        ville.setId(1);
        ville.setNom("Paris");
        ville.setPopulation(2000000);

        VilleDto villeDto = new VilleDto();
        villeDto.setId(1);
        villeDto.setNom("Paris");
        villeDto.setPopulation(2000000);

        when(villeService.extractVille(1)).thenReturn(ville);
        when(villeMappers.toDto(ville)).thenReturn(villeDto);

        mockMvc.perform(get("/ville/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Paris"))
                .andExpect(jsonPath("$.population").value(2000000));
    }

    /**
     * Vérifie que POST /ville avec des données valides crée bien la ville (status 200).
     */
    @Test
    void insertVille_devraitReussir_siDonneesValides() throws Exception {
        when(villeMappers.toBean(any(VilleDto.class))).thenReturn(new Ville());
        when(villeService.insertVille(any(Ville.class), any(), any())).thenReturn(List.of(new Ville()));

        String jsonValide = """
                {
                    "nom": "Paris",
                    "population": 2000000
                }
                """;

        mockMvc.perform(post("/ville")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValide))
                .andExpect(status().isOk())
                .andExpect(content().string("Ville insérée avec succès"));
    }

    /**
     * Vérifie que POST /ville avec des données invalides (nom trop court, population à 0)
     * est bien rejeté avec un status 400, sans même appeler le service.
     */
    @Test
    void insertVille_devraitEchouer_siDonneesInvalides() throws Exception {
        String jsonInvalide = """
                {
                    "nom": "P",
                    "population": 0
                }
                """;

        mockMvc.perform(post("/ville")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalide))
                .andExpect(status().isBadRequest());
    }
}