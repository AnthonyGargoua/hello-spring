package fr.diginamic.hello.services;

import fr.diginamic.hello.dao.DepartementDao;
import fr.diginamic.hello.dao.VilleDao;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// @Service, signale à Spring que cette classe est la couche métier (contrôles + orchestration), gérée comme un bean au même titre que @Repository ou @RestController
/**
 * Couche métier pour la gestion des villes.
 * <p>
 * Applique les contrôles métier (validation, unicité du nom) avant de déléguer les accès aux données à {@link VilleDao}.
 */
@Service
public class VilleService {
    private final VilleDao villeDao;
    private final DepartementDao departementDao;


    // Constructeur
    /**
     * Crée le service en lui injectant la DAO des villes et celle des départements.
     *
     * @param villeDao DAO utilisée pour accéder aux données des villes
     * @param departementDao DAO utilisée pour accéder aux données des départements
     */
    public VilleService(VilleDao villeDao, DepartementDao departementDao) {
        this.villeDao = villeDao;
        this.departementDao = departementDao;
    }

    // Je regroupe les contrôles métier communs à l'ajout et la modification (nom, population)
    /**
     * Vérifie que les données d'une ville respectent les règles métier (nom et population).
     *
     * @param ville ville à valider
     * @throws VilleException si le nom ou la population ne respecte pas les règles métier
     */
    private void validerVille (Ville ville) throws VilleException {
        if(ville.getPopulation() == null || ville.getPopulation() < 10){
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
        if(ville.getNom() == null || ville.getNom().length() < 2){
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
    }

    // Ici, je détermine le département à rattacher à une ville, à partir de son code et/ou de son id
    // Priorité à l'id s'il est fourni ; s'il ne correspond à rien, je retente avec le code ; si le code ne correspond à rien non plus, je crée le département
    // Si aucun des deux n'est fourni, ou si seul l'id est fourni sans correspondre à rien, j'échoue avec une exception
    /**
     * Résout le département à associer à une ville, à partir d'un code et/ou d'un identifiant de département.
     * <p>
     * Si l'identifiant est fourni et correspond à un département existant, celui-ci est utilisé.
     * Sinon, si le code est fourni, le département correspondant est recherché, et créé automatiquement s'il n'existe pas encore.
     *
     * @param codeDepartement code du département (peut être {@code null})
     * @param idDepartement identifiant du département (peut être {@code null})
     * @return le département trouvé ou créé
     * @throws VilleException si aucun des deux n'est fourni, ou si l'id fourni ne correspond à aucun département et qu'aucun code n'est fourni
     */
    private Departement resolverDepartement(String codeDepartement, Integer idDepartement) throws VilleException {
        if (codeDepartement == null && idDepartement == null){
            throw new VilleException("Il faut renseigner le code ou l'identifiant du département");
        }

        Departement departement = null;

        if (idDepartement != null){
            departement = departementDao.extractById(idDepartement);
        }

        if (departement == null && codeDepartement != null){
            departement = departementDao.extractByCode(codeDepartement);
            if (departement == null){
                departement = new Departement();
                departement.setCode(codeDepartement);
                departementDao.insert(departement);
            }
        }

        if (departement == null){
            throw new VilleException("Département inconnu");
        }

        return departement;
    }

    // Méthodes d'extraction, je délègue directement à la DAO, aucun contrôle métier nécessaire ici (simple lecture)
    /**
     * Récupère toutes les villes.
     *
     * @return la liste de toutes les villes
     */
    public List<Ville> extractVilles(){
        return villeDao.extractAll();
    }

    /**
     * Récupère une ville par son identifiant.
     *
     * @param idVille identifiant de la ville recherchée
     * @return la ville correspondante, ou {@code null} si aucune ville ne correspond à cet id
     */
    public Ville extractVille(int idVille){
        return villeDao.extractById(idVille);
    }

    /**
     * Récupère les villes dont le nom commence par le suffixe donné.
     *
     * @param suffixe début du nom recherché
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(String suffixe){
        return villeDao.extractBySuffixe(suffixe);
    }

    /**
     * Récupère les villes dont la population dépasse un minimum donné.
     *
     * @param min population minimale
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(int min){
        return villeDao.extractByMin(min);
    }

    /**
     * Récupère les villes dont la population est comprise entre deux valeurs données.
     *
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(int min, int max){
        return villeDao.extractByMinMax(min, max);
    }

    /**
     * Récupère les n villes les plus peuplées d'un département donné.
     *
     * @param idDepartement identifiant du département
     * @param n nombre de villes à renvoyer
     * @return la liste des n villes les plus peuplées du département
     */
    public List<Ville> extractTopNVillesParDepartement(int idDepartement, int n){
        return villeDao.extractTopNByDepartement(idDepartement, n);
    }

    /**
     * Récupère les villes d'un département donné dont la population est comprise entre deux valeurs données.
     *
     * @param idDepartement identifiant du département
     * @param min population minimale
     * @param max population maximale
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVillesParDepartementEtMinMax(int idDepartement, int min, int max){
        return villeDao.extractByDepartementAndMinMax(idDepartement, min, max);
    }

    // @Transactional, si une étape échoue (doublon détecté, département inconnu), rien n'est écrit en base
    // Je résous d'abord le département (trouvé ou créé), je le rattache à la ville, puis je valide le format et l'unicité du nom avant d'insérer
    /**
     * Résout le département associé, valide puis insère une nouvelle ville, après avoir vérifié qu'aucune ville existante ne porte déjà ce nom.
     *
     * @param ville ville à insérer
     * @param codeDepartement code du département à associer (peut être {@code null} si idDepartement est fourni)
     * @param idDepartement identifiant du département à associer (peut être {@code null} si codeDepartement est fourni)
     * @return la liste de toutes les villes après insertion
     * @throws VilleException si la ville est invalide, si le département est inconnu, ou si une ville du même nom existe déjà
     */
    @Transactional
    public List<Ville> insertVille(Ville ville, String codeDepartement, Integer idDepartement) throws VilleException{
        Departement departement = resolverDepartement(codeDepartement, idDepartement);
        ville.setDepartement(departement);

        validerVille(ville);

        List<Ville> villes = villeDao.extractAll();
        boolean existeDeja = villes.stream().anyMatch(v -> v.getNom().equals(ville.getNom()));
        if (existeDeja){
            throw new VilleException("Une ville avec ce nom existe déjà");
        }
        villeDao.insert(ville);
        return villeDao.extractAll();
    }

    // Je récupère la ville existante par son id, je vérifie qu'elle existe, puis je modifie ses champs avant de sauvegarder
    /**
     * Valide puis met à jour une ville existante.
     *
     * @param idVille identifiant de la ville à modifier
     * @param villeModifiee ville portant les nouvelles données
     * @return la liste de toutes les villes après modification
     * @throws VilleException si la ville est invalide ou si aucune ville ne correspond à l'id
     */
    @Transactional
    public List<Ville> updateVille(int idVille, Ville villeModifiee) throws VilleException{
        validerVille(villeModifiee);
        Ville villeExistante = villeDao.extractById(idVille);
        if (villeExistante == null){
            throw new VilleException("Aucune ville trouvée pour l'id " + idVille);
        }
        villeExistante.setNom(villeModifiee.getNom());
        villeExistante.setPopulation(villeModifiee.getPopulation());
        villeDao.update(villeExistante);
        return villeDao.extractAll();
    }

    // Je récupère la ville existante par son id, je vérifie qu'elle existe, puis je la supprime
    /**
     * Supprime une ville existante.
     *
     * @param idVille identifiant de la ville à supprimer
     * @return la liste de toutes les villes après suppression
     * @throws VilleException si aucune ville ne correspond à l'id
     */
    @Transactional
    public List<Ville> removeVille(int idVille) throws VilleException{
        Ville villeExistante = villeDao.extractById(idVille);
        if (villeExistante == null){
            throw new VilleException("Aucune ville trouvée pour l'id " + idVille);
        }
        villeDao.remove(villeExistante);
        return villeDao.extractAll();
    }
}