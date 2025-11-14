// Fichier : src/main/java/com/moscepa/service/ChapitreService.java (Version mise à jour)

package com.moscepa.service;

// ... (vos imports restent les mêmes)
import com.moscepa.dto.ChapitreAvecSectionsDto;
import com.moscepa.dto.ChapitreDetailDto;
import com.moscepa.dto.ChapitrePayload;
import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.SectionPayload;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Section;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.ElementConstitutifRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapitreService {

    @Autowired
    private ChapitreRepository chapitreRepository;
    
    @Autowired
    private ElementConstitutifRepository ecRepository;

    // --- VOS MÉTHODES EXISTANTES (INCHANGÉES) ---

    @Transactional(readOnly = true)
    public ChapitreDetailDto getChapitreDetailsPourTest(Long id) {
        Chapitre chapitre = chapitreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + id));
        return new ChapitreDetailDto(chapitre);
    }

    @Transactional
    public ChapitreDetailDto creerChapitreAvecNomMatiere(ChapitrePayload payload) {
        // ... (votre logique existante est inchangée)
        ElementConstitutif ecParent = ecRepository.findByNom(payload.getMatiere())
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec le nom: " + payload.getMatiere()));

        Chapitre nouveauChapitre = new Chapitre();
        nouveauChapitre.setNom(payload.getTitre());
        nouveauChapitre.setObjectif(payload.getObjectif());
        nouveauChapitre.setNiveau(payload.getNiveau());
        nouveauChapitre.setElementConstitutif(ecParent);

        if (payload.getSections() != null) {
            int ordreCompteur = 1;
            for (SectionPayload sectionPayload : payload.getSections()) {
                Section nouvelleSection = new Section();
                nouvelleSection.setTitre(sectionPayload.getTitre());
                nouvelleSection.setContenu("");
                nouvelleSection.setOrdre(ordreCompteur++);
                nouveauChapitre.addSection(nouvelleSection);
            }
        }

        Chapitre chapitreSauvegarde = chapitreRepository.save(nouveauChapitre);
        return new ChapitreDetailDto(chapitreSauvegarde);
    }

    @Transactional(readOnly = true)
    public Optional<ChapitreAvecSectionsDto> findChapitreCompletByMatiereAndNiveau(String matiereNom, Integer niveau) {
        return chapitreRepository.findByElementConstitutifNomAndNiveau(matiereNom, niveau)
            .map(ChapitreAvecSectionsDto::new);
    }

    @Transactional(readOnly = true)
    public Optional<ChapitreDetailDto> findByMatiereNomAndNiveau(String matiereNom, Integer niveau) {
        return chapitreRepository.findByElementConstitutifNomAndNiveau(matiereNom, niveau)
            .map(ChapitreDetailDto::new);
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsPourChapitre(Long chapitreId) {
        // ... (votre logique existante est inchangée)
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + chapitreId));
        
        return chapitre.getQuestionnaires().stream()
            .flatMap(questionnaire -> questionnaire.getQuestions().stream())
            .map(QuestionDto::new)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<ChapitreDetailDto> getChapitreDetailsById(Long id) {
        return chapitreRepository.findById(id).map(ChapitreDetailDto::new);
    }

    // ====================================================================
    // === NOUVELLE MÉTHODE AJOUTÉE POUR LA PAGE DE DÉTAIL DE L'ÉTUDIANT   ===
    // ====================================================================
    /**
     * Récupère un chapitre par son ID avec tous les détails de ses sections.
     * Utilise une requête optimisée du repository pour charger toutes les données en une fois.
     * @param id L'ID du chapitre.
     * @return Un Optional contenant le DTO complet du chapitre.
     */
    @Transactional(readOnly = true)
    public Optional<ChapitreAvecSectionsDto> findChapitreCompletById(Long id) {
        // On appellera ici la nouvelle méthode du repository
        return chapitreRepository.findChapitreCompletById(id)
                .map(ChapitreAvecSectionsDto::new); // On convertit l'entité en DTO
    }
}
