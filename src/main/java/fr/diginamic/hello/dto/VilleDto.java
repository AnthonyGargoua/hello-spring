package fr.diginamic.hello.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Ici, je crée une classe simple (un DTO) qui ne contient que ce dont le front a besoin sur une ville
// Contrairement à l'entité Ville, je n'ai aucune annotation JPA et aucun lien vers Departement, pour éviter la boucle infinie de sérialisation
// Je garde les mêmes annotations de validation que sur Ville, pour que @Valid continue de fonctionner dans le contrôleur
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
    public VilleDto() {
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

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getCodeDepartement() {
        return codeDepartement;
    }

    public void setCodeDepartement(String codeDepartement) {
        this.codeDepartement = codeDepartement;
    }

    public Integer getIdDepartement() {
        return idDepartement;
    }

    public void setIdDepartement(Integer idDepartement) {
        this.idDepartement = idDepartement;
    }
}