package fr.diginamic.hello.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO représentant un département tel que renvoyé par l'API externe https://geo.api.gouv.fr/departements.
 * <p>
 * Ne contient que les champs utiles (code, nom) ; les autres champs du JSON (codeRegion, etc.) sont ignorés
 * grâce à {@code @JsonIgnoreProperties(ignoreUnknown = true)}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DepartementApiDto {

    private String code;
    private String nom;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}