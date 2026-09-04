package fr.diginamic.hello.repositories;

import fr.diginamic.hello.entities.Departement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA pour l'entité {@link Departement}.
 * <p>
 * Remplace DepartementDao : les méthodes ci-dessous sont générées automatiquement par Spring Data.
 */
public interface DepartementRepository extends JpaRepository<Departement, Integer> {

    /**
     * Recherche un département à partir de son code.
     *
     * @param code code du département recherché
     * @return le département trouvé, ou {@code null} si aucun ne correspond
     */
    Departement findByCode(String code);
}