package fr.diginamic.hello.controleurs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * Service fournissant un message de salutation.
 */
@Service
public class HelloService {

    /**
     * Fournit le message de salutation.
     *
     * @return le message de salutation
     */
    @Bean
    public String salutations(){
        return "Je suis la classe de service et je vous dis Bonjour ";
    }
}
