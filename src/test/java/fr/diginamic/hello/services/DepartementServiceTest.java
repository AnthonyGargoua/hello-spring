package fr.diginamic.hello.services;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.DepartementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de {@link DepartementService}, avec Mockito.
 * <p>
 * Contrairement à {@link VilleServiceTest} (Spring Test + H2), aucun contexte Spring ni base
 * de données réelle n'est démarré ici : {@link DepartementRepository} est entièrement simulé
 * ({@code @Mock}), et on teste uniquement la logique métier de {@link DepartementService} en
 * isolation. Le {@code @PostConstruct} {@code initData()} ne se déclenche pas non plus ici,
 * puisqu'il n'y a pas de conteneur Spring pour l'exécuter.
 */
@ExtendWith(MockitoExtension.class)
class DepartementServiceTest {

    /** Faux repository : on définit nous-mêmes ce qu'il renvoie pour chaque test. */
    @Mock
    private DepartementRepository departementRepository;

    /** Service testé, avec le mock injecté automatiquement à la place du vrai repository. */
    @InjectMocks
    private DepartementService departementService;

    private Departement departementValide;

    @BeforeEach
    void setUp() {
        // Préparer : un département valide réutilisé dans plusieurs tests
        departementValide = new Departement();
        departementValide.setId(1);
        departementValide.setCode("75");
        departementValide.setNom("Paris");
    }

    /** Vérifie que extractDepartements renvoie bien la liste fournie par le repository. */
    @Test
    void extractDepartements_devraitRenvoyerLaListe() {
        when(departementRepository.findAll()).thenReturn(List.of(departementValide));

        List<Departement> resultat = departementService.extractDepartements();

        assertEquals(1, resultat.size());
        assertEquals("Paris", resultat.get(0).getNom());
    }

    /** Vérifie que extractDepartement renvoie le département quand l'id existe. */
    @Test
    void extractDepartement_devraitRenvoyerLeDepartement_siIdExiste() {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departementValide));

        Departement resultat = departementService.extractDepartement(1);

        assertNotNull(resultat);
        assertEquals("Paris", resultat.getNom());
    }

    /** Vérifie que extractDepartement renvoie null quand l'id est inconnu. */
    @Test
    void extractDepartement_devraitRenvoyerNull_siIdInconnu() {
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        Departement resultat = departementService.extractDepartement(999);

        assertNull(resultat);
    }

    /** Vérifie que extractDepartementParCode renvoie bien le département correspondant au code. */
    @Test
    void extractDepartementParCode_devraitRenvoyerLeDepartement() {
        when(departementRepository.findByCode("75")).thenReturn(departementValide);

        Departement resultat = departementService.extractDepartementParCode("75");

        assertNotNull(resultat);
        assertEquals("75", resultat.getCode());
    }

    /** Vérifie qu'un département valide est bien sauvegardé (appel à save() effectué). */
    @Test
    void insertDepartement_devraitReussir_siDonneesValides() throws VilleException {
        departementService.insertDepartement(departementValide);

        verify(departementRepository, times(1)).save(departementValide);
    }

    /** Vérifie que l'insertion échoue avec une VilleException si le nom est vide. */
    @Test
    void insertDepartement_devraitEchouer_siNomVide() {
        Departement departement = new Departement();
        departement.setCode("75");
        departement.setNom("");

        assertThrows(VilleException.class, () -> departementService.insertDepartement(departement));
        verify(departementRepository, never()).save(any());
    }

    /** Vérifie que l'insertion échoue avec une VilleException si le code est vide. */
    @Test
    void insertDepartement_devraitEchouer_siCodeVide() {
        Departement departement = new Departement();
        departement.setCode("");
        departement.setNom("Paris");

        assertThrows(VilleException.class, () -> departementService.insertDepartement(departement));
        verify(departementRepository, never()).save(any());
    }

    /** Vérifie que la modification met bien à jour les champs et sauvegarde, si le département existe. */
    @Test
    void updateDepartement_devraitModifier_siDepartementExiste() throws VilleException {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departementValide));

        Departement nouvellesDonnees = new Departement();
        nouvellesDonnees.setCode("75");
        nouvellesDonnees.setNom("Ile-de-France");

        departementService.updateDepartement(1, nouvellesDonnees);

        assertEquals("Ile-de-France", departementValide.getNom());
        verify(departementRepository, times(1)).save(departementValide);
    }

    /** Vérifie que la modification échoue avec une VilleException si l'id est inconnu. */
    @Test
    void updateDepartement_devraitEchouer_siDepartementInconnu() {
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        Departement nouvellesDonnees = new Departement();
        nouvellesDonnees.setCode("75");
        nouvellesDonnees.setNom("Ile-de-France");

        assertThrows(VilleException.class, () -> departementService.updateDepartement(999, nouvellesDonnees));
        verify(departementRepository, never()).save(any());
    }

    /** Vérifie que la suppression appelle bien delete() si le département existe. */
    @Test
    void removeDepartement_devraitSupprimer_siDepartementExiste() throws VilleException {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departementValide));

        departementService.removeDepartement(1);

        verify(departementRepository, times(1)).delete(departementValide);
    }

    /** Vérifie que la suppression échoue avec une VilleException si l'id est inconnu. */
    @Test
    void removeDepartement_devraitEchouer_siDepartementInconnu() {
        when(departementRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(VilleException.class, () -> departementService.removeDepartement(999));
        verify(departementRepository, never()).delete(any());
    }
}