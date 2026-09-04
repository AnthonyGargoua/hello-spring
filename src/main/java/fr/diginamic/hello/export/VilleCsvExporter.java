package fr.diginamic.hello.export;

import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Génère le fichier CSV d'export des villes (TP 12).
 * <p>
 * Isolé dans son propre package pour ne pas alourdir {@code VilleControleur}, qui se contente d'appeler
 * {@link #genererCsv(List)} et de poser les en-têtes HTTP de téléchargement.
 */
@Component
public class VilleCsvExporter {

    /**
     * Construit le contenu CSV (nom, population, code département, nom du département) pour une liste de villes.
     *
     * @param villes villes à exporter
     * @return le contenu du fichier CSV, encodé en UTF-8 avec BOM (pour un affichage correct des accents dans Excel)
     */
    public byte[] genererCsv(List<Ville> villes){
        // Le BOM UTF-8 en tête de fichier permet à Excel d'afficher correctement les accents
        StringBuilder csv = new StringBuilder("﻿");
        csv.append("Nom de la ville;Nombre d'habitants;Code département;Nom du département\n");

        for(Ville ville : villes){
            Departement departement = ville.getDepartement();
            csv.append(echapper(ville.getNom())).append(';')
                    .append(ville.getPopulation()).append(';')
                    .append(echapper(departement != null ? departement.getCode() : "")).append(';')
                    .append(echapper(departement != null ? departement.getNom() : "")).append('\n');
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Échappe une valeur pour l'insérer dans une cellule CSV (délimiteur ';', guillemets, retours à la ligne).
     *
     * @param valeur valeur à échapper
     * @return la valeur échappée, prête à être insérée dans le fichier CSV
     */
    private String echapper(String valeur){
        if(valeur == null){
            return "";
        }
        if(valeur.contains(";") || valeur.contains("\"") || valeur.contains("\n")){
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        return valeur;
    }
}
