// Fichier : src/main/java/com/moscepa/service/ProgressionService.java (Version Correcte)

package com.moscepa.service;

import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressionService {

    private final UtilisateurRepository utilisateurRepository;

    public ProgressionService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional(readOnly = true)
    public List<MatiereStatutDto> findMatieresByEtudiant(Long utilisateurId) {
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Aucun profil étudiant trouvé pour l'ID: " + utilisateurId));

        return etudiant.getMatieresInscrites().stream()
                .map(this::convertToDto)
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
