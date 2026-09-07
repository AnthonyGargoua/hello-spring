package fr.diginamic.hello.services;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.DepartementRepository;
import fr.diginamic.hello.repositories.VilleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de {@link VilleService}.
 * <p>
 * S'appuie sur Spring Test (@SpringBootTest) pour démarrer un vrai contexte Spring,
 * et sur le profil "test" (@ActiveProfiles) qui bascule la base de données sur H2 en mémoire
 * (voir application-test.properties) : aucune donnée réelle n'est lue ni modifiée.
 * <p>
 * Chaque test suit le pattern Arrange / Act / Assert (Préparer / Agir / Vérifier) et repart
 * d'une base vidée à chaque fois (voir {@link #setUp()}), pour que les tests soient indépendants
 * les uns des autres.
 */
@SpringBootTest
@ActiveProfiles("test")
class VilleServiceTest {

    /** Service testé : c'est lui qu'on appelle dans chaque test (couche métier réelle, pas un mock). */
    @Autowired
    private VilleService villeService;

    /** Repository des villes, utilisé pour vérifier directement l'état de la base après une action. */
    @Autowired
    private VilleRepository villeRepository;

    /** Repository des départements, utilisé pour préparer un département de référence avant chaque test. */
    @Autowired
    private DepartementRepository departementRepository;

    /** Département de référence recréé avant chaque test, auquel on rattache les villes de test. */
    private Departement departementTest;

    /**
     * Réinitialise la base H2 avant chaque test.
     * <p>
     * Supprime toutes les villes et tous les départements existants, puis recrée un département
     * de référence ("75" - Paris) auquel les villes créées dans les tests pourront être rattachées.
     * Cette remise à zéro garantit qu'un test ne dépend jamais des données laissées par un autre.
     */
    @BeforeEach
    void setUp() {
        // Préparer : on repart d'une base vide à chaque test, avec un département de référence
        villeRepository.deleteAll();
        departementRepository.deleteAll();

        departementTest = new Departement();
        departementTest.setCode("75");
        departementTest.setNom("Paris");
        departementRepository.save(departementTest);
    }

    /**
     * Vérifie qu'une ville avec des données valides (nom correct, population suffisante,
     * département existant) est bien insérée en base.
     */
    @Test
    void insertVille_devraitReussir_siDonneesValides() throws VilleException {
        // Préparer : une ville valide à rattacher au département de test
        Ville ville = new Ville();
        ville.setNom("Paris");
        ville.setPopulation(1000000);

        // Agir : on demande au service de l'insérer
        List<Ville> resultat = villeService.insertVille(ville, null, departementTest.getId());

        // Vérifier : la liste renvoyée contient bien la nouvelle ville, et elle existe en base
        assertEquals(1, resultat.size());
        assertTrue(villeRepository.existsByNom("Paris"));
    }

    /**
     * Vérifie que l'ajout d'une ville échoue avec une {@link VilleException} si une ville
     * porte déjà ce nom (règle métier de {@code validerVille}/{@code existsByNom}).
     */
    @Test
    void insertVille_devraitEchouer_siNomDejaExistant() throws VilleException {
        // Préparer : une première ville "Lyon" déjà insérée en base
        Ville ville1 = new Ville();
        ville1.setNom("Lyon");
        ville1.setPopulation(500000);
        villeService.insertVille(ville1, null, departementTest.getId());

        // Une seconde ville portant le même nom
        Ville ville2 = new Ville();
        ville2.setNom("Lyon");
        ville2.setPopulation(100000);

        // Agir + Vérifier : l'insertion du doublon doit lever une VilleException
        assertThrows(VilleException.class, () ->
                villeService.insertVille(ville2, null, departementTest.getId()));
    }

    /**
     * Vérifie que l'ajout d'une ville échoue avec une {@link VilleException} si sa population
     * est inférieure au minimum autorisé (10 habitants, règle définie dans {@code validerVille}).
     */
    @Test
    void insertVille_devraitEchouer_siPopulationTropFaible() {
        // Préparer : une ville avec une population largement sous le seuil minimum
        Ville ville = new Ville();
        ville.setNom("Petiteville");
        ville.setPopulation(5);

        // Agir + Vérifier : l'insertion doit être rejetée
        assertThrows(VilleException.class, () ->
                villeService.insertVille(ville, null, departementTest.getId()));
    }

    /**
     * Vérifie que la recherche d'une ville par un identifiant inexistant renvoie {@code null}
     * plutôt que de lever une exception (comportement attendu de {@code extractVille}).
     */
    @Test
    void extractVille_devraitRenvoyerNull_siIdInconnu() {
        // Agir + Vérifier : aucun id 999999 ne peut exister sur une base H2 fraîchement vidée
        assertNull(villeService.extractVille(999999));
    }

    /**
     * Vérifie que la modification d'une ville existante met bien à jour ses champs en base
     * (ici la population).
     */
    @Test
    void updateVille_devraitModifierLaVille() throws VilleException {
        // Préparer : une ville existante en base
        Ville ville = new Ville();
        ville.setNom("Marseille");
        ville.setPopulation(800000);
        villeService.insertVille(ville, null, departementTest.getId());
        Ville villeEnBase = villeRepository.findByNomStartingWith("Marseille").get(0);

        // Agir : on modifie sa population
        Ville villeModifiee = new Ville();
        villeModifiee.setNom("Marseille");
        villeModifiee.setPopulation(900000);
        villeService.updateVille(villeEnBase.getId(), villeModifiee);

        // Vérifier : la nouvelle population est bien celle attendue
        Ville resultat = villeService.extractVille(villeEnBase.getId());
        assertEquals(900000, resultat.getPopulation());
    }

    /**
     * Vérifie que la suppression d'une ville existante la fait bien disparaître de la base.
     */
    @Test
    void removeVille_devraitSupprimerLaVille() throws VilleException {
        // Préparer : une ville existante en base
        Ville ville = new Ville();
        ville.setNom("Nice");
        ville.setPopulation(340000);
        villeService.insertVille(ville, null, departementTest.getId());
        Ville villeEnBase = villeRepository.findByNomStartingWith("Nice").get(0);

        // Agir : on la supprime
        villeService.removeVille(villeEnBase.getId());

        // Vérifier : elle n'est plus trouvable
        assertNull(villeService.extractVille(villeEnBase.getId()));
    }
}