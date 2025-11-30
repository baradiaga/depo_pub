// Fichier : src/main/java/com/moscepa/service/BanqueQuestionService.java

package com.moscepa.service;

import com.moscepa.repository.TagRepository;
import com.moscepa.dto.BanqueQuestionCreationDto;
import com.moscepa.dto.BanqueQuestionDetailDto;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import com.moscepa.security.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BanqueQuestionService {

    private final BanqueQuestionRepository banqueQuestionRepository;
    private final BanqueReponseRepository banqueReponseRepository;
    private final ChapitreRepository chapitreRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final TagRepository tagRepository;

    public BanqueQuestionService(BanqueQuestionRepository banqueQuestionRepository, BanqueReponseRepository banqueReponseRepository, ChapitreRepository chapitreRepository, UtilisateurRepository utilisateurRepository, TagRepository tagRepository) {
        this.banqueQuestionRepository = banqueQuestionRepository;
        this.banqueReponseRepository = banqueReponseRepository;
        this.chapitreRepository = chapitreRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.tagRepository = tagRepository;
    }

    // ====================================================================
    // === CRÉATION ET MISE À JOUR                                      ===
    // ====================================================================

    @Transactional
    public BanqueQuestionDetailDto creerQuestion(BanqueQuestionCreationDto dto) {
        return sauvegarderQuestion(new BanqueQuestion(), dto);
    }

    @Transactional
    public BanqueQuestionDetailDto mettreAJourQuestion(Long id, BanqueQuestionCreationDto dto) {
        BanqueQuestion question = banqueQuestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question de banque non trouvée avec l'ID: " + id));
        return sauvegarderQuestion(question, dto);
    }

    private BanqueQuestionDetailDto sauvegarderQuestion(BanqueQuestion question, BanqueQuestionCreationDto dto) {
        // 1. Récupération des entités liées
        Chapitre chapitre = chapitreRepository.findById(dto.getChapitreId())
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + dto.getChapitreId()));

        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Utilisateur auteur = utilisateurRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new EntityNotFoundException("Auteur non trouvé."));

        // 2. Mise à jour des propriétés de la question
        question.setEnonce(dto.getEnonce());
        question.setTypeQuestion(dto.getTypeQuestion());
        question.setPoints(dto.getPoints());
        question.setDifficulte(dto.getDifficulte());
        question.setChapitre(chapitre);

        if (question.getId() == null) {
            question.setAuteur(auteur);
            question.setStatut(StatutQuestion.BROUILLON); // Nouvelle question en brouillon
        }

        // 3. Gestion des Tags
       Set<Tag> tags = dto.getTags().stream()
    .map(nom -> tagRepository.findByNomIgnoreCase(nom)
        .orElseGet(() -> {
            Tag newTag = new Tag(nom);
            return tagRepository.save(newTag);
        }))
    .collect(Collectors.toSet());

question.setTags(tags);


        // 4. Gestion des Réponses (Suppression et Recréation)
        question.getReponses().clear(); // Supprime les anciennes réponses (via orphanRemoval)
        dto.getReponses().forEach(reponseDto -> {
            BanqueReponse reponse = new BanqueReponse();
            reponse.setTexte(reponseDto.getTexte());
            reponse.setCorrecte(reponseDto.getCorrecte());
            reponse.setBanqueQuestion(question);
            question.getReponses().add(reponse);
        });

        // 5. Sauvegarde
        BanqueQuestion questionSauvegardee = banqueQuestionRepository.save(question);

        return new BanqueQuestionDetailDto(questionSauvegardee);
    }

    // ====================================================================
    // === LECTURE ET RECHERCHE                                         ===
    // ====================================================================

    public BanqueQuestionDetailDto getQuestionById(Long id) {
        BanqueQuestion question = banqueQuestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question de banque non trouvée avec l'ID: " + id));
        return new BanqueQuestionDetailDto(question);
    }

    public List<BanqueQuestionDetailDto> getAllQuestions() {
        return banqueQuestionRepository.findAll().stream()
                .map(BanqueQuestionDetailDto::new)
                .collect(Collectors.toList());
    }
    // ====================================================================
    // === SUPPRESSION                                                  ===
    // ====================================================================

    @Transactional
    public void supprimerQuestion(Long id) {
        BanqueQuestion question = banqueQuestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question de banque non trouvée avec l'ID: " + id));

        
        // Si oui, soit on bloque la suppression, soit on supprime la référence dans les Questionnaires.
        // Pour l'instant, nous allons supposer qu'elle n'est pas référencée.

        banqueQuestionRepository.delete(question);
    }

    // ====================================================================
    // === STATISTIQUES ET ÉVALUATION (Méthodes de mise à jour)         ===
    // ====================================================================

    @Transactional
    public void mettreAJourStatistiques(Long questionId, boolean reponseCorrecte) {
        BanqueQuestion question = banqueQuestionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question de banque non trouvée avec l'ID: " + questionId));

   
        question.incrementerUtilisations();
        banqueQuestionRepository.save(question);
    }

    
}
