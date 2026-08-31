package fr.diginamic.hello.entities;

public class Ville {

    String nom;
    Double population;

    // Constructeur
    public Ville(String nom, Double population) {
        this.nom = nom;
        this.population = population;
    }

    // Getters & Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Double getPopulation() {
        return population;
    }

    public void setPopulation(Double population) {
        this.population = population;
    }
}