package com.moscepa.service;

import com.moscepa.dto.ElementConstitutifRequestDto;
import com.moscepa.dto.ElementConstitutifResponseDto; // <-- NOUVEL IMPORT
import com.moscepa.dto.EnseignantDto; // <-- NOUVEL IMPORT
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.UniteEnseignementRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // <-- NOUVEL IMPORT

@Service
public class ElementConstitutifService {

    @Autowired
    private ElementConstitutifRepository elementRepository;

    @Autowired
    private UniteEnseignementRepository ueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * MODIFIÉ : Crée un nouvel EC et retourne un DTO de réponse complet.
     */
    @Transactional
    public ElementConstitutifResponseDto creerPourUe(Long ueId, ElementConstitutifRequestDto dto) {
        UniteEnseignement ue = ueRepository.findById(ueId)
                .orElseThrow(() -> new EntityNotFoundException("Unité d'Enseignement non trouvée avec l'ID: " + ueId));

        Utilisateur enseignant = null;
        if (dto.getEnseignantId() != null) {
            enseignant = utilisateurRepository.findById(dto.getEnseignantId())
                    .orElseThrow(() -> new EntityNotFoundException("Enseignant non trouvé avec l'ID: " + dto.getEnseignantId()));
        }

        ElementConstitutif nouvelElement = new ElementConstitutif();
        nouvelElement.setNom(dto.getNom());
        nouvelElement.setCode(dto.getCode());
        nouvelElement.setDescription(dto.getDescription());
        nouvelElement.setCredit(dto.getCredit());
        nouvelElement.setUniteEnseignement(ue);
        nouvelElement.setEnseignant(enseignant);

        ElementConstitutif savedElement = elementRepository.save(nouvelElement);
        
        // On retourne le DTO de réponse complet
        return convertToResponseDto(savedElement);
    }

    /**
     * MODIFIÉ : Récupère la liste des ECs et les retourne en tant que DTOs de réponse.
     */
    @Transactional(readOnly = true)
    public List<ElementConstitutifResponseDto> findByUeId(Long ueId) {
        List<ElementConstitutif> elements = elementRepository.findByUniteEnseignementId(ueId);
        
        // On convertit la liste d'entités en liste de DTOs de réponse
        return elements.stream()
                       .map(this::convertToResponseDto)
                       .collect(Collectors.toList());
    }

    // --- NOUVELLE MÉTHODE DE CONVERSION ---

    /**
     * Convertit une entité ElementConstitutif en son DTO de réponse pour l'affichage.
     */
    private ElementConstitutifResponseDto convertToResponseDto(ElementConstitutif element) {
        ElementConstitutifResponseDto dto = new ElementConstitutifResponseDto();
        dto.setId(element.getId());
        dto.setNom(element.getNom());
        dto.setCode(element.getCode());
        dto.setDescription(element.getDescription());
        dto.setCredit(element.getCredit());

        // LA CORRECTION PRINCIPALE EST ICI
        if (element.getEnseignant() != null) {
            // Si un enseignant est associé, on crée son DTO et on l'ajoute
            EnseignantDto enseignantDto = new EnseignantDto(
                element.getEnseignant().getId(),
                element.getEnseignant().getNom(),
                element.getEnseignant().getPrenom()
            );
            dto.setEnseignant(enseignantDto);
        } else {
            // Sinon, on met null
            dto.setEnseignant(null);
        }

        return dto;
    }
}
