package fr.diginamic.hello.services;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import fr.diginamic.hello.exceptions.VilleException;
import fr.diginamic.hello.repositories.DepartementRepository;
import fr.diginamic.hello.repositories.VilleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service métier pour l'entité {@link Ville}.
 * <p>
 * Regroupe les contrôles de validation, la résolution du département associé,
 * et l'orchestration des appels à {@link VilleRepository}.
 */
@Service
public class VilleService {
    private final VilleRepository villeRepository;
    private final DepartementRepository departementRepository;

    /**
     * Construit le service à partir de ses dépendances.
     *
     * @param villeRepository repository Spring Data JPA pour l'entité Ville
     * @param departementRepository repository Spring Data JPA pour l'entité Departement
     */
    public VilleService(VilleRepository villeRepository, DepartementRepository departementRepository) {
        this.villeRepository = villeRepository;
        this.departementRepository = departementRepository;
    }

    /**
     * Vérifie qu'une ville respecte les règles métier (population et nom).
     *
     * @param ville ville à valider
     * @throws VilleException si la population est absente/inférieure à 10, ou si le nom est absent/trop court
     */
    private void validerVille (Ville ville) throws VilleException {
        if(ville.getPopulation() == null || ville.getPopulation() < 10){
            throw new VilleException("La ville doit avoir au moins 10 habitants");
        }
        if(ville.getNom() == null || ville.getNom().length() < 2){
            throw new VilleException("Le nom de la ville doit contenir au moins 2 lettres");
        }
    }

    /**
     * Résout le département à rattacher à une ville à partir de son code et/ou de son id.
     * <p>
     * Priorité à l'id s'il est fourni ; s'il ne correspond à rien, retente avec le code ;
     * si le code ne correspond à rien non plus, crée le département.
     *
     * @param codeDepartement code du département (peut être {@code null})
     * @param idDepartement identifiant du département (peut être {@code null})
     * @return le département résolu (existant ou nouvellement créé)
     * @throws VilleException si ni le code ni l'id ne sont fournis, ou si aucun département ne peut être résolu
     */
    private Departement resolverDepartement(String codeDepartement, Integer idDepartement) throws VilleException {
        if (codeDepartement == null && idDepartement == null){
            throw new VilleException("Il faut renseigner le code ou l'identifiant du département");
        }

        Departement departement = null;

        if (idDepartement != null){
            departement = departementRepository.findById(idDepartement).orElse(null);
        }

        if (departement == null && codeDepartement != null){
            departement = departementRepository.findByCode(codeDepartement);
            if (departement == null){
                departement = new Departement();
                departement.setCode(codeDepartement);
                departementRepository.save(departement);
            }
        }

        if (departement == null){
            throw new VilleException("Département inconnu");
        }

        return departement;
    }

    /**
     * Récupère les villes de façon paginée.
     *
     * @param page numéro de la page souhaitée (0 = première page)
     * @param size nombre de villes par page
     * @return la page de villes correspondante
     */
    public Page<Ville> extractVillesPaginees(int page, int size){
        return villeRepository.findAll(PageRequest.of(page, size));
    }

    /**
     * Récupère une ville à partir de son identifiant.
     *
     * @param idVille identifiant de la ville recherchée
     * @return la ville trouvée, ou {@code null} si aucune ville ne correspond
     */
    public Ville extractVille(int idVille){
        return villeRepository.findById(idVille).orElse(null);
    }

    /**
     * Recherche les villes dont le nom commence par la chaîne donnée.
     *
     * @param prefixe début du nom recherché
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(String prefixe){
        return villeRepository.findByNomStartingWith(prefixe);
    }

    /**
     * Recherche les villes dont la population dépasse un minimum donné, triées par population décroissante.
     *
     * @param min population minimale (exclusive)
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(int min){
        return villeRepository.findByPopulationGreaterThanOrderByPopulationDesc(min);
    }

    /**
     * Recherche les villes dont la population est comprise entre min et max, triées par population décroissante.
     *
     * @param min population minimale (exclusive)
     * @param max population maximale (exclusive)
     * @return la liste des villes correspondantes
     */
    public List<Ville> extractVilles(int min, int max){
        return villeRepository.findByPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(min, max);
    }

    /**
     * Recherche les villes d'un département dont la population dépasse un minimum donné, triées par population décroissante.
     *
     * @param idDepartement identifiant du département
     * @param min population minimale (exclusive)
     * @return la liste des villes correspondantes, ou une liste vide si le département n'existe pas
     */
    public List<Ville> extractVillesParDepartementEtMin(int idDepartement, int min){
        Departement departement = departementRepository.findById(idDepartement).orElse(null);
        if (departement == null){
            return List.of();
        }
        return villeRepository.findByDepartementAndPopulationGreaterThanOrderByPopulationDesc(departement, min);
    }

    /**
     * Recherche les n villes les plus peuplées d'un département donné.
     *
     * @param idDepartement identifiant du département
     * @param n nombre de villes à renvoyer
     * @return la liste des n villes les plus peuplées du département, ou une liste vide si le département n'existe pas
     */
    public List<Ville> extractTopNVillesParDepartement(int idDepartement, int n){
        Departement departement = departementRepository.findById(idDepartement).orElse(null);
        if (departement == null){
            return List.of();
        }
        return villeRepository.findByDepartementOrderByPopulationDesc(departement, PageRequest.of(0, n));
    }

    /**
     * Recherche les villes d'un département dont la population est comprise entre min et max, triées par population décroissante.
     *
     * @param idDepartement identifiant du département
     * @param min population minimale (exclusive)
     * @param max population maximale (exclusive)
     * @return la liste des villes correspondantes, ou une liste vide si le département n'existe pas
     */
    public List<Ville> extractVillesParDepartementEtMinMax(int idDepartement, int min, int max){
        Departement departement = departementRepository.findById(idDepartement).orElse(null);
        if (departement == null){
            return List.of();
        }
        return villeRepository.findByDepartementAndPopulationGreaterThanAndPopulationLessThanOrderByPopulationDesc(departement, min, max);
    }

    /**
     * Ajoute une nouvelle ville, en résolvant d'abord son département (trouvé ou créé).
     *
     * @param ville ville à créer
     * @param codeDepartement code du département à rattacher (peut être {@code null})
     * @param idDepartement identifiant du département à rattacher (peut être {@code null})
     * @return la liste de toutes les villes après insertion
     * @throws VilleException si le département ne peut pas être résolu, si la ville est invalide, ou si son nom existe déjà
     */
    @Transactional
    public List<Ville> insertVille(Ville ville, String codeDepartement, Integer idDepartement) throws VilleException{
        Departement departement = resolverDepartement(codeDepartement, idDepartement);
        ville.setDepartement(departement);

        validerVille(ville);

        if (villeRepository.existsByNom(ville.getNom())){
            throw new VilleException("Une ville avec ce nom existe déjà");
        }
        villeRepository.save(ville);
        return villeRepository.findAll();
    }

    /**
     * Modifie une ville existante (nom et population).
     *
     * @param idVille identifiant de la ville à modifier
     * @param villeModifiee ville portant les nouvelles données
     * @return la liste de toutes les villes après modification
     * @throws VilleException si les nouvelles données sont invalides ou si aucune ville ne correspond à l'id
     */
    @Transactional
    public List<Ville> updateVille(int idVille, Ville villeModifiee) throws VilleException{
        validerVille(villeModifiee);
        Ville villeExistante = villeRepository.findById(idVille).orElse(null);
        if (villeExistante == null){
            throw new VilleException("Aucune ville trouvée pour l'id " + idVille);
        }
        villeExistante.setNom(villeModifiee.getNom());
        villeExistante.setPopulation(villeModifiee.getPopulation());
        villeRepository.save(villeExistante);
        return villeRepository.findAll();
    }

    /**
     * Supprime une ville à partir de son identifiant.
     *
     * @param idVille identifiant de la ville à supprimer
     * @return la liste de toutes les villes après suppression
     * @throws VilleException si aucune ville ne correspond à l'id
     */
    @Transactional
    public List<Ville> removeVille(int idVille) throws VilleException{
        Ville villeExistante = villeRepository.findById(idVille).orElse(null);
        if (villeExistante == null){
            throw new VilleException("Aucune ville trouvée pour l'id " + idVille);
        }
        villeRepository.delete(villeExistante);
        return villeRepository.findAll();
    }
}