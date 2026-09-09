package fr.diginamic.hello.security;

import fr.diginamic.hello.repositories.RoleRepository;
import fr.diginamic.hello.repositories.UtilisateurRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @PostConstruct
    public void initUtilisateur(){
        if (utilisateurRepository.findByUsername("gdupont") == null) {
            Role role = new Role("USER");
            roleRepository.save(role);
            Utilisateur gdupont = new Utilisateur ("gdupont", encoder.encode("user1234"));
            gdupont.addRole(role);
            utilisateurRepository.save(gdupont);
        }
        if (utilisateurRepository.findByUsername("aduval") == null){
            Role role = new Role("ADMIN");
            roleRepository.save(role);
            Utilisateur aduval = new Utilisateur ("aduval", encoder.encode("admin1234"));
            aduval.addRole(role);
            utilisateurRepository.save(aduval);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByUsername(username);
        if (utilisateur == null) {
            throw new UsernameNotFoundException("Utilisateur inconnu.");
        }
        return utilisateur;
    }
}
