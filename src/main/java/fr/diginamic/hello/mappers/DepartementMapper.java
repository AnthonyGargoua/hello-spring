package fr.diginamic.hello.mappers;

import fr.diginamic.hello.dto.DepartementDto;
import fr.diginamic.hello.entities.Departement;
import org.springframework.stereotype.Component;

// @Component, je signale à Spring de gérer cette classe comme un bean, pour pouvoir l'injecter plus tard dans DepartementControleur
@Component
public class DepartementMapper {

    // Ici, je recopie chaque champ de Departement vers DepartementDto, pour transformer une entité en objet transmissible au front
    public DepartementDto toDto(Departement departement){
        DepartementDto dto = new DepartementDto();
        dto.setId(departement.getId());
        dto.setNom(departement.getNom());
        dto.setCode(departement.getCode());
        return dto;
    }

    // Ici, je fais l'inverse : je recopie chaque champ de DepartementDto vers Departement, pour reconstruire une entité à partir de ce que le front a envoyé
    public Departement toBean(DepartementDto dto){
        Departement departement = new Departement();
        departement.setId(dto.getId());
        departement.setNom(dto.getNom());
        departement.setCode(dto.getCode());
        return departement;
    }
}