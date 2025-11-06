// Fichier : src/main/java/com/moscepa/service/SyllabusService.java (Version Complète et Finale)

package com.moscepa.service;

import com.moscepa.dto.ChapitreSyllabusDto;
import com.moscepa.dto.MatiereSyllabusDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.repository.ElementConstitutifRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyllabusService {

    @Autowired
    private ElementConstitutifRepository ecRepository;

    @Transactional(readOnly = true)
    public MatiereSyllabusDto getSyllabusPourEtudiant(Long ecId, Long utilisateurId) {
        
        ElementConstitutif ec = ecRepository.findById(ecId)
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + ecId));

        // 1. Créer le DTO principal manuellement
        MatiereSyllabusDto matiereDto = new MatiereSyllabusDto();
        matiereDto.setId(ec.getId());
        matiereDto.setNom(ec.getNom());
        matiereDto.setCode(ec.getCode());
        matiereDto.setDescription(ec.getDescription());

        // 2. Transformer chaque entité Chapitre en son DTO
        List<ChapitreSyllabusDto> chapitresDto = ec.getChapitres().stream()
            .map(chapitre -> convertToChapitreSyllabusDto(chapitre, utilisateurId))
            .sorted(Comparator.comparing(ChapitreSyllabusDto::getOrdre, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

        // 3. Attacher la liste des chapitres au DTO principal
        matiereDto.setChapitres(chapitresDto);
        
        return matiereDto;
    }

    /**
     * Méthode privée pour convertir une entité Chapitre en son DTO pour le syllabus.
     */
    private ChapitreSyllabusDto convertToChapitreSyllabusDto(Chapitre chapitre, Long utilisateurId) {
        ChapitreSyllabusDto dto = new ChapitreSyllabusDto();
        dto.setId(chapitre.getId());
        dto.setNom(chapitre.getNom());
        dto.setOrdre(chapitre.getOrdre());

       
        

        // Nom du test
        if (chapitre.getQuestionnaires() != null && !chapitre.getQuestionnaires().isEmpty()) {
            dto.setNomTest(chapitre.getQuestionnaires().get(0).getTitre());
        } else {
            dto.setNomTest("Non défini");
        }

        // Résultat du test (simulé)
        if (chapitre.getOrdre() != null && chapitre.getOrdre() <= 2) {
            dto.setResultatScore(Math.random() * 40 + 60);
        } else {
            dto.setResultatScore(null);
        }

        // Catégorie (simulée)
        dto.setCategorie("Concepts Clés");

        return dto;
    }
}
