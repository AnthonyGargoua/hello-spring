package fr.diginamic.hello.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Ici, je crée une classe simple (un DTO) qui ne contient que ce dont le front a besoin sur un département
// Contrairement à l'entité Departement, je n'ai aucune annotation JPA et aucun lien vers Ville, pour éviter la boucle infinie de sérialisation
// Je garde les mêmes annotations de validation que sur Departement, pour que @Valid continue de fonctionner dans le contrôleur
/**
 * DTO représentant un département dans les échanges avec l'extérieur (front, Postman...).
 */
public class DepartementDto {

    private int id;
    @NotNull
    @Size(min = 2)
    private String nom;
    @NotNull
    private String code;

    // Constructeur vide, je le laisse pour que Jackson puisse créer l'objet avant de le remplir avec les setters
    /**
     * Constructeur vide requis par Jackson.
     */
    public DepartementDto() {
    }

    // Getters & Setters, je les utilise pour lire et modifier chaque champ depuis l'extérieur de la classe
    /**
     * @return l'identifiant du département
     */
    public int getId() {
        return id;
    }

    /**
     * @param id identifiant à affecter au département
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return le nom du département
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom nom à affecter au département
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return le code du département
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code code à affecter au département
     */
    public void setCode(String code) {
        this.code = code;
    }
}