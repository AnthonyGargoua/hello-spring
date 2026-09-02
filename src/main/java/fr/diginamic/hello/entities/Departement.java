package fr.diginamic.hello.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

/**
 * Entité JPA représentant un département.
 * <p>
 * Un département possède un code, un nom et regroupe plusieurs {@link Ville villes}.
 */
@Entity
public class Departement {

    /** Identifiant unique du département, auto-incrémenté par la base de données. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /** Code du département. */
    private String code;
    /** Nom du département. */
    private String nom;

    // Un département possède plusieurs villes. mappedBy pointe vers le champ "departement" dans la classe Ville
    // @JsonIgnore évite la boucle infinie. Departement -> villes -> departement -> villes -> ect...

    /** Liste des villes rattachées à ce département. */
    @OneToMany(mappedBy = "departement")
    @JsonIgnore
    private List<Ville> villes;

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
     * @return la liste des villes du département
     */
    public List<Ville> getVilles() {
        return villes;
    }

    /**
     * @param villes liste des villes à affecter au département
     */
    public void setVilles(List<Ville> villes) {
        this.villes = villes;
    }
}
