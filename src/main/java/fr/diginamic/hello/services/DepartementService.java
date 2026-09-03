package fr.diginamic.hello.services;

import fr.diginamic.hello.dao.DepartementDao;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.exceptions.VilleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service, je signale à Spring que cette classe est la couche métier (contrôles + orchestration), gérée comme un bean au même titre que @Repository ou @RestController
@Service
public class DepartementService {

    private final DepartementDao departementDao;

    // Ici, je crée le service en lui injectant la DAO des départements
    public DepartementService(DepartementDao departementDao) {
        this.departementDao = departementDao;
    }

    // Ici, je regroupe les contrôles métier communs à l'ajout et la modification (nom, code)
    private void validerDepartement(Departement departement) throws VilleException{
        if(departement.getNom() == null || departement.getNom().isEmpty()){
            throw new VilleException("Le nom du département doit avoir au moins 2 lettres");
        }
        if(departement.getCode() == null || departement.getCode().isEmpty()){
            throw new VilleException("Le code du département doit être renseigné");
        }
    }

    // Ici, je délègue directement à la DAO, aucun contrôle métier nécessaire (simple lecture)
    public List<Departement> extractDepartements(){
        return departementDao.extractAll();
    }

    // Ici, je récupère un seul département par son id, je délègue directement à la DAO
    public Departement extractDepartement(int id){
        return departementDao.extractById(id);
    }

    // Ici, je récupère un seul département par son code, je délègue directement à la DAO
    public Departement extractDepartementParCode(String code){
        return departementDao.extractByCode(code);
    }

    // @Transactional, si une étape échoue (validation ratée), rien n'est écrit en base
    // Je valide le département avant de l'insérer
    @Transactional
    public void insertDepartement(Departement departement) throws VilleException{
        validerDepartement(departement);
        departementDao.insert(departement);
    }

    // @Transactional, si une étape échoue (validation ratée), rien n'est écrit en base
    // Ici, je récupère le département existant par son id, je vérifie qu'il existe, puis je modifie ses champs avant de sauvegarder
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
    @Transactional
    public void removeDepartement(int id) throws VilleException{
        Departement existant = departementDao.extractById(id);
        if (existant == null){
            throw new VilleException("Le departement n'existe pas");
        }
        departementDao.remove(existant);
    }
}