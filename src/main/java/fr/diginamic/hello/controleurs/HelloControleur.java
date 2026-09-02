package fr.diginamic.hello.controleurs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST exposant un point d'accès de salutation.
 */
@RestController
@RequestMapping ("/hello")
public class HelloControleur {

    private final HelloService service;

    // Constructeur
    /**
     * Crée le contrôleur en lui injectant le service de salutations.
     *
     * @param service service utilisé pour générer la salutation
     */
    public HelloControleur(HelloService service) {
        this.service = service;
    }

    // Sans constructeur
    // Mettre juste @Autowired et private HelloService service;

    /**
     * Renvoie un message de salutation.
     *
     * @return le message de salutation
     */
    @GetMapping
    public String direHello(){
        return service.salutations();
    }
}
