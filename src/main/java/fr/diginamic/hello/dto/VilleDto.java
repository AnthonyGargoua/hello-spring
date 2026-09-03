package fr.diginamic.hello.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Ici, je crée une classe simple (un DTO) qui ne contient que ce dont le front a besoin sur une ville
// Contrairement à l'entité Ville, je n'ai aucune annotation JPA et aucun lien vers Departement, pour éviter la boucle infinie de sérialisation
// Je garde les mêmes annotations de validation que sur Ville, pour que @Valid continue de fonctionner dans le contrôleur
/**
 * DTO représentant une ville dans les échanges avec l'extérieur (front, Postman...).
 * <p>
 * Porte aussi {@code codeDepartement} et {@code idDepartement}, utilisés uniquement en entrée pour
 * rattacher la ville à un département existant, ou en créer un nouveau si le code est inconnu.
 */
public class VilleDto {

    private int id;
    @NotNull
    @Size(min = 2)
    private String nom;
    @NotNull
    @Min(1)
    private Integer population;
    private String codeDepartement;
    private Integer idDepartement;

    // Constructeur vide, je le laisse pour que Jackson puisse créer l'objet avant de le remplir avec les setters
    /**
     * Constructeur vide requis par Jackson.
     */
    public VilleDto() {
    }

    // Getters & Setters, je les utilise pour lire et modifier chaque champ depuis l'extérieur de la classe
    /**
     * @return l'identifiant de la ville
     */
    public int getId() {
        return id;
    }

    /**
     * @param id identifiant à affecter à la ville
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return le nom de la ville
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom nom à affecter à la ville
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return la population de la ville
     */
    public Integer getPopulation() {
        return population;
    }

    /**
     * @param population population à affecter à la ville
     */
    public void setPopulation(Integer population) {
        this.population = population;
    }

    /**
     * @return le code du département à associer à la ville (utilisé en entrée uniquement)
     */
    public String getCodeDepartement() {
        return codeDepartement;
    }

    /**
     * @param codeDepartement code du département à associer à la ville
     */
    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    /**
     * @return l'identifiant du département à associer à la ville (utilisé en entrée uniquement)
     */
    public Integer getIdDepartement() {
        return idDepartement;
    }

    /**
     * @param idDepartement identifiant du département à associer à la ville
     */
    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }
}