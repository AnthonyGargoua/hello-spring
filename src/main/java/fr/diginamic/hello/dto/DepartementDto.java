package fr.diginamic.hello.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Ici, je crée une classe simple (un DTO) qui ne contient que ce dont le front a besoin sur un département
// Contrairement à l'entité Departement, je n'ai aucune annotation JPA et aucun lien vers Ville, pour éviter la boucle infinie de sérialisation
// Je garde les mêmes annotations de validation que sur Departement, pour que @Valid continue de fonctionner dans le contrôleur
public class DepartementDto {

    private int id;
    @NotNull
    @Size(min = 2)
    private String nom;
    @NotNull
    private String code;

    // Constructeur vide, je le laisse pour que Jackson puisse créer l'objet avant de le remplir avec les setters
    public DepartementDto() {
    }

    // Getters & Setters, je les utilise pour lire et modifier chaque champ depuis l'extérieur de la classe
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}