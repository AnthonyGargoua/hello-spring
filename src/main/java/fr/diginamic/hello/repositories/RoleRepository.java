package fr.diginamic.hello.repositories;

import fr.diginamic.hello.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
