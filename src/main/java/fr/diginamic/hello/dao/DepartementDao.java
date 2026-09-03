package fr.diginamic.hello.dao;


import fr.diginamic.hello.entities.Departement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository, je signale à Spring que cette classe est une DAO, il va la gérer comme un bean (comme @Service ou @RestController)
/**
 * Couche d'accès aux données pour l'entité {@link Departement}.
 * <p>
 * Utilise directement l'{@link EntityManager} JPA pour effectuer les opérations CRUD et les recherches.
 */
@Repository
public class DepartementDao {

    // @PersistenceContext injecte l'EntityManager, je laisse Spring gérer tout seul son cycle de vie
    /** EntityManager JPA injecté par Spring, utilisé pour toutes les opérations de persistance. */
    @PersistenceContext
    private EntityManager entityManager;

    // Ici, j'extrais tous les départements présents en base
    /**
     * Extrait tous les départements présents en base.
     *
     * @return la liste de tous les départements
     */
    public List<Departement> extractAll(){
        TypedQuery<Departement> query = entityManager.createQuery("SELECT d FROM Departement d", Departement.class);
        return query.getResultList();
    }

    // Ici, j'extrais un département par son id, em.find va directement chercher par clé primaire
    /**
     * Extrait un département à partir de son identifiant.
     *
     * @param id identifiant du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à cet id
     */
    public Departement extractById(int id){
        return entityManager.find(Departement.class, id);
    }

    // Ici, j'extrais un département par son code, je ne peux pas utiliser em.find car il ne fonctionne que par clé primaire (l'id)
    // je construis donc une requête JPQL, je lui donne la valeur du code avec setParameter
    // getResultList() ne plante jamais si rien n'est trouvé (contrairement à getSingleResult), donc je renvoie null si la liste est vide
    /**
     * Extrait un département à partir de son code.
     *
     * @param code code du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à ce code
     */
    public Departement extractByCode(String code){
        TypedQuery<Departement> query = entityManager.createQuery("SELECT d FROM Departement d WHERE d.code = :code", Departement.class);
        query.setParameter("code", code);
        List<Departement> resultats = query.getResultList();
        return resultats.isEmpty() ? null : resultats.get(0);
    }

    // Ici, j'insère un nouveau département en base
    /**
     * Insère un nouveau département en base.
     *
     * @param departement département à insérer
     */
    public void insert(Departement departement){
        entityManager.persist(departement); // persist me permet d'insérer un nouvel objet en base (INSERT)
    }

    // Ici, je mets à jour un département déjà existant en base
    /**
     * Met à jour un département existant en base.
     *
     * @param departement département portant les données à jour
     */
    public void update(Departement departement){
        entityManager.merge(departement); // merge me permet de mettre à jour un objet déjà existant en base (UPDATE)
    }

    // Ici, je supprime un département de la base
    /**
     * Supprime un département de la base.
     *
     * @param departement département à supprimer
     */
    public void remove(Departement departement){
        entityManager.remove(departement); // remove me permet de supprimer un objet (DELETE)
    }
}