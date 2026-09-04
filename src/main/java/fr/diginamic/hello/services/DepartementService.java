package fr.diginamic.hello.services;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.DepartementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Couche métier pour la gestion des départements.
 * <p>
 * Applique les contrôles métier (validation du nom et du code) avant de déléguer les accès aux données à {@link DepartementRepository}.
 */
@Service
public class DepartementService {

    private final DepartementRepository departementRepository;

    /**
     * Crée le service en lui injectant le repository des départements.
     *
     * @param departementRepository repository utilisé pour accéder aux données des départements
     */
    public DepartementService(DepartementRepository departementRepository) {
        this.departementRepository = departementRepository;
    }

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

    /**
     * Récupère tous les départements.
     *
     * @return la liste de tous les départements
     */
    public List<Departement> extractDepartements(){
        return departementRepository.findAll();
    }

    /**
     * Récupère un département par son identifiant.
     *
     * @param id identifiant du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à cet id
     */
    public Departement extractDepartement(int id){
        return departementRepository.findById(id).orElse(null);
    }

    /**
     * Récupère un département par son code.
     *
     * @param code code du département recherché
     * @return le département correspondant, ou {@code null} si aucun département ne correspond à ce code
     */
    public Departement extractDepartementParCode(String code){
        return departementRepository.findByCode(code);
    }

    /**
     * Valide puis insère un nouveau département.
     *
     * @param departement département à insérer
     * @throws VilleException si le département est invalide
     */
    @Transactional
    public void insertDepartement(Departement departement) throws VilleException{
        validerDepartement(departement);
        departementRepository.save(departement);
    }

    /**
     * Valide puis met à jour un département existant.
     *
     * @param id identifiant du département à modifier
     * @param departement département portant les nouvelles données
     * @throws VilleException si le département est invalide ou si aucun département ne correspond à l'id
     */
    @Transactional
    public void updateDepartement(int id, Departement departement) throws VilleException{
        Departement existant = departementRepository.findById(id).orElse(null);
        if (existant == null){
            throw new VilleException("Le département n'existe pas");
        }
        validerDepartement(departement);
        existant.setNom(departement.getNom());
        existant.setCode(departement.getCode());
        departementRepository.save(existant);
    }

    /**
     * Supprime un département existant.
     *
     * @param id identifiant du département à supprimer
     * @throws VilleException si aucun département ne correspond à l'id
     */
    @Transactional
    public void removeDepartement(int id) throws VilleException{
        Departement existant = departementRepository.findById(id).orElse(null);
        if (existant == null){
            throw new VilleException("Le departement n'existe pas");
        }
        departementRepository.delete(existant);
    }
}