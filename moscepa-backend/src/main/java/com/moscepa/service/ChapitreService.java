// Fichier : src/main/java/com/moscepa/service/ChapitreService.java (Version Finale Corrigée avec les nouveaux champs)

package com.moscepa.service;
import com.moscepa.entity.ResultatTest;

import com.moscepa.dto.*;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.Section;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.ElementConstitutifRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChapitreService {

    private static final Logger log = LoggerFactory.getLogger(ChapitreService.class);

    @Autowired
    private ChapitreRepository chapitreRepository;
    
    @Autowired
    private ElementConstitutifRepository ecRepository;

    // --- VOS MÉTHODES EXISTANTES ---

    @Transactional(readOnly = true)
    public ChapitreDetailDto getChapitreDetailsPourTest(Long id) {
        Chapitre chapitre = chapitreRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + id));
        return new ChapitreDetailDto(chapitre);
    }

    @Transactional
    public ChapitreDetailDto creerChapitreAvecNomMatiere(ChapitrePayload payload) {
        ElementConstitutif ecParent = ecRepository.findByNom(payload.getMatiere())
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec le nom: " + payload.getMatiere()));

        Chapitre nouveauChapitre = new Chapitre();
        nouveauChapitre.setNom(payload.getTitre());
        nouveauChapitre.setObjectif(payload.getObjectif());
        nouveauChapitre.setNiveau(payload.getNiveau());
        nouveauChapitre.setElementConstitutif(ecParent);
        
        // ========== AJOUT DES NOUVEAUX CHAMPS ==========
        nouveauChapitre.setTypeActivite(payload.getTypeActivite());
        nouveauChapitre.setPrerequis(payload.getPrerequis());
        nouveauChapitre.setTypeEvaluation(payload.getTypeEvaluation());
        // ===============================================

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

    @Transactional(readOnly = true)
    public Optional<ChapitreAvecSectionsDto> findChapitreCompletById(Long id) {
        return chapitreRepository.findChapitreCompletById(id)
                .map(ChapitreAvecSectionsDto::new);
    }

    @Transactional(readOnly = true)
    public List<ChapitreContenuDto> findContenuCompletPourMatiere(Long matiereId) {
        if (!ecRepository.existsById(matiereId)) {
            throw new EntityNotFoundException("Matière non trouvée avec l'ID: " + matiereId);
        }
        List<Chapitre> chapitres = chapitreRepository.findAllChapitresCompletsByMatiereId(matiereId);
        return chapitres.stream()
                        .map(ChapitreContenuDto::new)
                        .collect(Collectors.toList());
    }

    @Transactional
    public ChapitreContenuDto createChapitre(Long matiereId, ChapitreCreateDto dto) {
        ElementConstitutif matiere = ecRepository.findById(matiereId)
                .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec l'ID: " + matiereId));
        
        Integer nouvelOrdre = chapitreRepository.countByElementConstitutifId(matiereId) + 1;

        Chapitre nouveauChapitre = new Chapitre();
        nouveauChapitre.setNom(dto.nom);
        nouveauChapitre.setObjectif(dto.objectif);
        nouveauChapitre.setOrdre(nouvelOrdre);
        nouveauChapitre.setElementConstitutif(matiere);
        
        // ========== AJOUT DES NOUVEAUX CHAMPS (SI PRÉSENTS DANS LE DTO) ==========
        // Note : Pour l'instant, ChapitreCreateDto ne contient pas ces champs
        // Si tu veux les utiliser ici, il faut d'abord les ajouter au DTO
        // nouveauChapitre.setTypeActivite(dto.typeActivite);
        // nouveauChapitre.setPrerequis(dto.prerequis);
        // nouveauChapitre.setTypeEvaluation(dto.typeEvaluation);
        // =======================================================================

        Chapitre chapitreSauvegarde = chapitreRepository.save(nouveauChapitre);
        return new ChapitreContenuDto(chapitreSauvegarde);
    }

    @Transactional
    public void deleteChapitre(Long chapitreId) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Impossible de supprimer : Chapitre non trouvé avec l'ID: " + chapitreId));

        // Vérifier si des résultats existent pour les tests liés
        boolean hasResults = chapitre.getTests().stream()
            .anyMatch(test -> test.getResultats() != null && !test.getResultats().isEmpty());

        if (hasResults) {
            throw new IllegalStateException(
                "Suppression refusée : des résultats de tests existent pour ce chapitre.");
        }

        chapitreRepository.delete(chapitre);
        log.info("Chapitre avec l'ID {} supprimé avec succès.", chapitreId);
    }

    // ============================================================
    // === MÉTHODE DRY-RUN : LISTER LES RÉSULTATS BLOQUANTS =======
    // ============================================================
    @Transactional(readOnly = true)
    public List<ResultatTest> getResultatsPourChapitre(Long chapitreId) {
        Chapitre chapitre = chapitreRepository.findById(chapitreId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Chapitre non trouvé avec l'ID: " + chapitreId));

        return chapitre.getTests().stream()
            .flatMap(test -> test.getResultats().stream())
            .collect(Collectors.toList());
    }
    
}