package fr.diginamic.hello.repositories;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité {@link Ville}.
 * <p>
 * Remplace VilleDao : les méthodes ci-dessous sont générées automatiquement par Spring Data
 * à partir de leur nom (dérivation de requêtes), sans écrire de JPQL à la main.
 */
public interface VilleRepository extends JpaRepository<Ville,Integer> {

    // Recherche de toutes les villes dont le nom commence par une chaine de caractères données
    /**
     * Recherche les villes dont le nom commence par la chaîne donnée.
     *
     * @param prefixe début du nom recherché
     * @return la liste des villes correspondantes
     */
    List<Ville> findByNomStartingWith(String prefixe);

    /**
     * Vérifie si une ville portant ce nom existe déjà
     *
     * @param nom nom recherché
     * @return {@code true} si une ville porte ce nom
     */
    boolean existsByNom(String nom);

    // Recherche de toutes les villes dont la population est supérieure à min (paramètre de type int)
    // Les villes sont retournées par population descendante.
    /**
     * Recherche les villes dont la population est strictement supérieure à min, triées par population décroissante.
     *
     * @param min population minimale (exclusive)
     * @return la liste des villes correspondantes
     */
    List<Ville> findByPopulationGreaterThanOrderByPopulationDesc(int min);

    //Recherche de toutes les villes dont la population est supérieure à min et inférieure à max
    // Les villes sont retournées par population descendante
    /**
     * Recherche les villes dont la population est strictement comprise entre min et max, triées par population décroissante.
     *
     * @param min population minimale (exclusive)
     * @param max population maximale (exclusive)
     * @return la liste des villes correspondantes
     */
    List<Ville> findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(int min, int max);

    // Recherche de toutes les villes d’un département dont la population est supérieure à min
    // (paramètre de type int). Les villes sont retournées par population descendante
    /**
     * Recherche les villes d'un département dont la population est strictement supérieure à min, triées par population décroissante.
     *
     * @param departement département concerné
     * @param min population minimale (exclusive)
     * @return la liste des villes correspondantes
     */
    List<Ville> findByDepartementAndPopulationGreaterThanOrderByPopulationDesc(Departement departement, int min);

    // Recherche de toutes les villes d’un département dont la population est supérieure à min
    // et inférieure à max. Les villes sont retournées par population descendante
    /**
     * Recherche les villes d'un département dont la population est strictement comprise entre min et max, triées par population décroissante.
     *
     * @param departement département concerné
     * @param min population minimale (exclusive)
     * @param max population maximale (exclusive)
     * @return la liste des villes correspondantes
     */
    List<Ville> findByDepartementAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(Departement departement, int min, int max);

    // Recherche des n villes les plus peuplées d’un département donné (n est aussi un paramètre)
    /**
     * Recherche les villes d'un département, triées par population décroissante, en s'appuyant sur la pagination
     * pour ne récupérer que les n premières (n étant dynamique, findTopNBy exige un nombre fixe, donc inutilisable ici).
     *
     * @param departement département concerné
     * @param pageable pagination utilisée pour limiter le nombre de résultats (page 0, taille n)
     * @return la liste des villes correspondantes, limitée par la pagination
     */
    List<Ville> findByDepartementOrderByPopulationDesc(Departement departement, Pageable pageable);
}