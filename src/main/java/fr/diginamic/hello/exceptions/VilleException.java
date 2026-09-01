package fr.diginamic.hello.exceptions;

// Exception personnalisée, hérite de Exception (et non RuntimeException), donc c'est une exception "checked"
// Toute méthode qui la lève doit la déclarer avec "throws VilleException" dans sa signature.
public class VilleException extends Exception {

    public VilleException(String message){
        super(message);
    }
}
