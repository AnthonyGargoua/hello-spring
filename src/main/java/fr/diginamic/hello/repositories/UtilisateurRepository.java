package fr.diginamic.hello.repositories;

import fr.diginamic.hello.security.Utilisateur;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    @EntityGraph(attributePaths = "roles")
    Utilisateur findByUsername(String username);
}
