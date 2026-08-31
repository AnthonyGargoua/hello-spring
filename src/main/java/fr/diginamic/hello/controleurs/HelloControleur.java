package fr.diginamic.hello.controleurs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/hello")
public class HelloControleur {

    private final HelloService service;

    // Constructeur
    public HelloControleur(HelloService service) {
        this.service = service;
    }

    // Sans constructeur
    // Mettre juste @Autowired et private HelloService service;

    @GetMapping
    public String direHello(){
        return service.salutations();
    }
}
