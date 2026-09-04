package fr.diginamic.hello.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entité JPA représentant une ville.
 * <p>
 * Une ville possède un nom, une population et est rattachée à un {@link Departement}.
 */
@Entity
public class Ville {

    // Rajout de l'id
    // @Id désigne la clé primaire de la table, l'identifiant unique de chaque ville
    // @GeneratedValue(IDENTITY) délègue l'auto-incrémentation de cet id à MySQL, comme le faisait mon prochainId++ avant, mais côté base de données
    /** Identifiant unique de la ville, auto-incrémenté par la base de données. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /** Nom de la ville (2 caractères minimum, obligatoire). */
    @NotNull
    @Size(min = 2)
    private String nom;
    /** Population de la ville (doit être supérieure ou égale à 1). */
    @Min(1)
    @Column(name = "nb_habs")
    private Integer population;
    /** Département auquel appartient la ville. */
    @ManyToOne
    @JoinColumn(name = "id_dept")
    private Departement departement;

    // Constructeur vide
    /**
     * Constructeur vide requis par JPA.
     */
    public Ville() {

    }

    // Constructeur
    /**
     * Crée une ville à partir de son nom et de sa population.
     *
     * @param nom nom de la ville
     * @param population population de la ville
     */
    public Ville(String nom, Integer population) {
        this.nom = nom;
        this.population = population;
    }

    // Getters & Setters
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
     * @return le département de la ville
     */
    public Departement getDepartement() {
        return departement;
    }

    /**
     * @param departement département à affecter à la ville
     */
    public void setDepartement(Departement departement) {
        this.departement = departement;
    }
}