package fr.diginamic.hello.services;

import fr.diginamic.hello.dao.DepartementDao;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.exceptions.VilleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service, je signale à Spring que cette classe est la couche métier (contrôles + orchestration), gérée comme un bean au même titre que @Repository ou @RestController
/**
 * Couche métier pour la gestion des départements.
 * <p>
 * Applique les contrôles métier (validation du nom et du code) avant de déléguer les accès aux données à {@link DepartementDao}.
 */
@Service
public class DepartementService {

    private final DepartementDao departementDao;

    // Ici, je crée le service en lui injectant la DAO des départements
    /**
     * Crée le service en lui injectant la DAO des départements.
     *
     * @param departementDao DAO utilisée pour accéder aux données des départements
     */
    public DepartementService(DepartementDao departementDao) {
        this.departementDao = departementDao;
    }

    // Ici, je regroupe les contrôles métier communs à l'ajout et la modification (nom, code)
    /**
     * Vérifie que les données d'un département respectent les règles métier (nom et code).
     *
     * @param departement département à valider
     * @throws VilleException si le nom ou le code ne respecte pas les règles métier
     */
    private void validerDepartement(Departement departement) throws VilleException{
        if(departement.getNom() == null || departement.getNom().isEmpty()){
            throw new VilleException("Le nom du département doit avoir au moins 2 lettres");
        }
        if(departement.getCode() == null || departement.getCode().isEmpty()){
            throw new VilleException("Le code du département doit être renseigné");
        }
    }

    // Ici, je délègue directement à la DAO, aucun contrôle métier nécessaire (simple lecture)
    /**
     * Récupère tous les départements.
     *
     * @return la liste de tous les départements
     */
    public List<Departement> extractDepartements(){
        return departementDao.extractAll();
    }

    // Ici, je récupère un seul département par son id, je délègue directement à la DAO
    /**
     * Récupère un département par son identifiant.
     *
     * @param id identifiant du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à cet id
     */
    public Departement extractDepartement(int id){
        return departementDao.extractById(id);
    }

    // Ici, je récupère un seul département par son code, je délègue directement à la DAO
    /**
     * Récupère un département par son code.
     *
     * @param code code du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à ce code
     */
    public Departement extractDepartementParCode(String code){
        return departementDao.extractByCode(code);
    }

    // @Transactional, si une étape échoue (validation ratée), rien n'est écrit en base
    // Je valide le département avant de l'insérer
    /**
     * Valide puis insère un nouveau département.
     *
     * @param departement département à insérer
     * @throws VilleException si le département est invalide
     */
    @Transactional
    public void insertDepartement(Departement departement) throws VilleException{
        validerDepartement(departement);
        departementDao.insert(departement);
    }

    // @Transactional, si une étape échoue (validation ratée), rien n'est écrit en base
    // Ici, je récupère le département existant par son id, je vérifie qu'il existe, puis je modifie ses champs avant de sauvegarder
    /**
     * Valide puis met à jour un département existant.
     *
     * @param id identifiant du département à modifier
     * @param departement département portant les nouvelles données
     * @throws VilleException si le département est invalide ou si aucun département ne correspond à l'id
     */
    @Transactional
    public void updateDepartement(int id, Departement departement) throws VilleException{
        Departement existant = departementDao.extractById(id);
        if (existant == null){
            throw new VilleException("Le département n'existe pas");
        }
        validerDepartement(departement);
        existant.setNom(departement.getNom());
        existant.setCode(departement.getCode());
        departementDao.update(existant);
    }

    // @Transactional, si une étape échoue (validation ratée), rien n'est écrit en base
    // Ici, je récupère le département existant par son id, je vérifie qu'il existe, puis je le supprime
    /**
     * Supprime un département existant.
     *
     * @param id identifiant du département à supprimer
     * @throws VilleException si aucun département ne correspond à l'id
     */
    @Transactional
    public void removeDepartement(int id) throws VilleException{
        Departement existant = departementDao.extractById(id);
        if (existant == null){
            throw new VilleException("Le departement n'existe pas");
        }
        departementDao.remove(existant);
    }
}