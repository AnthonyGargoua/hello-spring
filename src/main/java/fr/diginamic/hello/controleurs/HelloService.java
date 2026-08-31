package fr.diginamic.hello.controleurs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    @Bean
    public String salutations(){
        return "Je suis la classe de service et je vous dis Bonjour ";
    }
}
