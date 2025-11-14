// Fichier : src/main/java/com/moscepa/service/ProgressionService.java (Version Finale Corrigée)

package com.moscepa.service;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.repository.ElementConstitutifRepository; // <-- IMPORT IMPORTANT
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressionService {

    // On injecte le REPOSITORY, pas le service, pour avoir accès aux entités
    private final ElementConstitutifRepository elementConstitutifRepository;

    public ProgressionService(ElementConstitutifRepository elementConstitutifRepository) {
        this.elementConstitutifRepository = elementConstitutifRepository;
    }

    /**
     * Trouve les matières d'un étudiant et les convertit en DTO de statut.
     * CORRECTION : Appelle directement le repository pour obtenir les entités.
     */
    @Transactional(readOnly = true)
    public List<MatiereStatutDto> findMatieresByEtudiant(Long utilisateurId) {
        // On appelle la méthode du REPOSITORY qui renvoie des ENTITÉS
        return elementConstitutifRepository.findMatieresByEtudiantIdSqlNatif(utilisateurId).stream()
                .map(this::convertToDto) // Maintenant, le type correspond !
                .collect(Collectors.toList());
    }

    private MatiereStatutDto convertToDto(ElementConstitutif ec) {
        return new MatiereStatutDto(
                ec.getId(),
                ec.getNom(),
                0,
                ec.getCode(),
                ec.getCredit(),
                null
        );
    }
}
