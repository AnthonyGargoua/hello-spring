package fr.diginamic.hello.dao;

import fr.diginamic.hello.entities.Ville;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository, signale à Spring que cette classe est une DAO, il va la gérer comme un bean (comme @Service ou @RestController)
/**
 * Couche d'accès aux données pour l'entité {@link Ville}.
 * <p>
 * Utilise directement l'{@link EntityManager} JPA pour effectuer les opérations CRUD et les recherches.
 */
@Repository
public class VilleDao {

    // @PersistenceContext injecte l'EntityManager, Spring gère tout seul son cycle de vie
    /** EntityManager JPA injecté par Spring, utilisé pour toutes les opérations de persistance. */
    @PersistenceContext
    private EntityManager entityManager;

    // Extrait toutes les villes en base
    /**
     * Extrait toutes les villes présentes en base.
     *
     * @return la liste de toutes les villes
     */
    public List<Ville> extractAll(){
        TypedQuery<Ville> query = entityManager.createQuery("SELECT v FROM Ville v", Ville.class);
        return query.getResultList();
    }

    // Extrait une ville par son id, em.find va directement chercher par clé primaire
    /**
     * Extrait une ville à partir de son identifiant.
     *
     * @param id identifiant de la ville recherchée
     * @return la ville correspondante, ou {@code null} si aucune ville ne correspond à cet id
     */
    public Ville extractById(int id){
        return entityManager.find(Ville.class, id);
    }

    // Extrait les villes dont le nom commence par un suffixe donné
    // :suffixe est un paramètre donné, je lui donne sa valeur juste après grâce à setParameter
    /**
     * Extrait les villes dont le nom commence par le suffixe donné.
     *
     * @param suffixe début du nom recherché
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractBySuffixe(String suffixe){
        TypedQuery<Ville> query = entityManager.createQuery("SELECT v FROM Ville v WHERE v.nom LIKE :suffixe", Ville.class);
        query.setParameter("suffixe", suffixe + "%");
        return query.getResultList();
    }

    // Extrait les villes dont la population est supérieur à min
    /**
     * Extrait les villes dont la population est strictement supérieure à un minimum donné.
     *
     * @param min population minimale (exclusive)
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractByMin(int min){
        TypedQuery<Ville> query = entityManager.createQuery("SELECT v FROM Ville v WHERE v.population > :min", Ville.class);
        query.setParameter("min", min);
        return query.getResultList();
    }

    // Extrait les villes dont la population est comprise entre min et max
    /**
     * Extrait les villes dont la population est comprise entre deux valeurs données (bornes incluses).
     *
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractByMinMax(int min, int max){
        TypedQuery<Ville> query = entityManager.createQuery("SELECT v FROM Ville v WHERE v.population BETWEEN :min AND :max", Ville.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    //  Insère une nouvelle ville en base et retourne la liste des villes après insertion
    /**
     * Insère une nouvelle ville en base.
     *
     * @param ville ville à insérer
     */
    public void insert(Ville ville){
        entityManager.persist(ville); // persist permet d'insérer un nouvel objet en base (INSERT)
    }

    // Modifie la ville dont l’identifiant est passé en paramètre. Les nouvelles données sont portées par l’instance villeModifiee
    // La méthode retourne la liste des villes après modification

    /**
     * Met à jour une ville existante en base.
     *
     * @param ville ville portant les données à jour
     */
    public void update(Ville ville){
        entityManager.merge(ville); // merge permet de mettre à jour un objet déjà existant en base (UPDATE)
    }

    // Supprime la ville dont ‘id est passé en paramètre et retourne la liste des villes après suppression
    /**
     * Supprime une ville de la base.
     *
     * @param ville ville à supprimer
     */
    public void remove(Ville ville){
        entityManager.remove(ville); // remove permet de supprimer un objet (DELETE)
    }
}