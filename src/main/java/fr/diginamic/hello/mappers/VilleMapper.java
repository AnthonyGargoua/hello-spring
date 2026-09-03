package fr.diginamic.hello.mappers;

import fr.diginamic.hello.dto.VilleDto;
import fr.diginamic.hello.entities.Ville;
import org.springframework.stereotype.Component;

// @Component, je signale à Spring de gérer cette classe comme un bean, pour pouvoir l'injecter plus tard dans VilleControleur
@Component
public class VilleMapper {

    // Ici, je recopie chaque champ de Ville vers VilleDto, pour transformer une entité en objet transmissible au front
    public VilleDto toDto(Ville ville) {
        VilleDto dto = new VilleDto();
        dto.setId(ville.getId());
        dto.setNom(ville.getNom());
        dto.setPopulation(ville.getPopulation());
        return dto;
    }

    // Ici, je fais l'inverse : je recopie chaque champ de VilleDto vers Ville, pour reconstruire une entité à partir de ce que le front a envoyé
    public Ville toBean(VilleDto dto){
        Ville ville = new Ville();
        ville.setId(dto.getId());
        ville.setNom(dto.getNom());
        ville.setPopulation(dto.getPopulation());
        return ville;
    }
}