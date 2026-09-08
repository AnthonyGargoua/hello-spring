package fr.diginamic.hello.security;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {

    private String name;

    public Role(String nom){
        this.name = "ROLE_" + nom;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
