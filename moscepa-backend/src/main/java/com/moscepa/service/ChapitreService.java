package com.moscepa.service;

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

    @Transactional
    public ChapitreDetailDto creerChapitreAvecNomMatiere(ChapitrePayload payload) {
        ElementConstitutif ecParent = ecRepository.findByNom(payload.getMatiere())
            .orElseThrow(() -> new EntityNotFoundException("Matière non trouvée avec le nom: " + payload.getMatiere()));

        Chapitre nouveauChapitre = new Chapitre();
        nouveauChapitre.setNom(payload.getTitre());
        nouveauChapitre.setObjectif(payload.getObjectif());
        nouveauChapitre.setNiveau(payload.getNiveau());
        nouveauChapitre.setElementConstitutif(ecParent);

        if (payload.getSections() != null) {
            int ordreCompteur = 1; // On initialise un compteur pour l'ordre
            for (SectionPayload sectionPayload : payload.getSections()) {
                Section nouvelleSection = new Section();
                nouvelleSection.setTitre(sectionPayload.getTitre());
                nouvelleSection.setContenu(""); // Contenu initial vide, c'est ok
                
                // On assigne l'ordre actuel et on incrémente le compteur
                nouvelleSection.setOrdre(ordreCompteur++); 

                nouveauChapitre.addSection(nouvelleSection);
            }
        }

        Chapitre chapitreSauvegarde = chapitreRepository.save(nouveauChapitre);
        return new ChapitreDetailDto(chapitreSauvegarde);
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
}
