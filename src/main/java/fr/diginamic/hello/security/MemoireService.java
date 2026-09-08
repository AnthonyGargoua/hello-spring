package fr.diginamic.hello.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemoireService implements UserDetailsService{

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equals("gdupont")) {
           return new Utilisateur(username, encoder.encode("user1234"), "USER");
        } else if (username.equals("aduval")) {
            return new Utilisateur(username, encoder.encode("admin1234"), "ADMIN");
        } else {
            throw new UsernameNotFoundException("Utilisateur inconnu.");
        }
    }
}
