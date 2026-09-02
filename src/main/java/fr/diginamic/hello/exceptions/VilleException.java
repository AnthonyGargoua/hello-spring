package fr.diginamic.hello.exceptions;

// Exception personnalisée, hérite de Exception (et non RuntimeException), donc c'est une exception "checked"
// Toute méthode qui la lève doit la déclarer avec "throws VilleException" dans sa signature.
/**
 * Exception métier levée en cas de donnée invalide ou de ville introuvable/déjà existante.
 */
public class VilleException extends Exception {

    /**
     * Crée une nouvelle exception métier liée aux villes.
     *
     * @param message message décrivant l'erreur
     */
    public VilleException(String message){
        super(message);
    }
}
