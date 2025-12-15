package com.moscepa.service;

import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ReponsePourQuestionDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Questionnaire;
import com.moscepa.entity.Question;
import com.moscepa.entity.Reponse;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.QuestionnaireRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private static final Logger logger = LoggerFactory.getLogger(QuestionnaireService.class);
    
    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    
    @Autowired
    private ChapitreRepository chapitreRepository;

    // ====================================================================
    // --- Récupérer tous les questionnaires avec leurs questions ---
    // ====================================================================
    @Transactional(readOnly = true)
    public List<QuestionnaireDetailDto> getAllQuestionnairesDetail() {
        logger.info("Service: Récupération de tous les questionnaires");
        
        return questionnaireRepository.findAll().stream()
                .map(this::convertToDtoWithQuestions)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // --- Récupérer un questionnaire spécifique par ID ---
    // ====================================================================
    @Transactional(readOnly = true)
    public QuestionnaireDetailDto getQuestionnaireDetailById(Long id) {
        logger.info("Service: Récupération du questionnaire ID: {}", id);
        
        Questionnaire questionnaire = questionnaireRepository
                .findByIdWithQuestions(id)
                .orElseThrow(() -> {
                    logger.error("Questionnaire non trouvé avec ID: {}", id);
                    return new RuntimeException("Questionnaire non trouvé avec l'ID: " + id);
                });
        
        logger.debug("Questionnaire trouvé: {}", questionnaire.getTitre());
        logger.debug("Nombre de questions: {}", 
            questionnaire.getQuestions() != null ? questionnaire.getQuestions().size() : 0);
        
        return new QuestionnaireDetailDto(questionnaire);
    }

    // ====================================================================
    // --- Créer un nouveau questionnaire (VERSION CORRIGÉE) ---
    // ====================================================================
    @Transactional
    public QuestionnaireDetailDto createQuestionnaire(QuestionnaireDetailDto dto) {
        logger.info("=== 🚀 DÉBUT CRÉATION QUESTIONNAIRE ===");
        logger.info("📋 Données reçues:");
        logger.info("  - Titre: {}", dto.getTitre());
        logger.info("  - ChapitreId: {}", dto.getChapitreId());
        logger.info("  - Durée: {}", dto.getDuree());
        logger.info("  - Description: {}", dto.getDescription());
        logger.info("  - Nombre de questions: {}", 
            dto.getQuestions() != null ? dto.getQuestions().size() : 0);
        
        // ======================
        // 1. VALIDATIONS
        // ======================
        
        // Titre obligatoire
        if (dto.getTitre() == null || dto.getTitre().trim().isEmpty()) {
            logger.error("❌ Titre manquant ou vide");
            throw new IllegalArgumentException("Le titre est obligatoire");
        }
        
        // Chapitre obligatoire
        if (dto.getChapitreId() == null) {
            logger.error("❌ chapitreId est null dans le DTO");
            throw new IllegalArgumentException("Le chapitre est obligatoire");
        }
        
        // ======================
        // 2. CHARGER LE CHAPITRE
        // ======================
        Chapitre chapitre = chapitreRepository.findById(dto.getChapitreId())
                .orElseThrow(() -> {
                    logger.error("❌ Chapitre non trouvé avec ID: {}", dto.getChapitreId());
                    return new IllegalArgumentException("Chapitre non trouvé avec ID: " + dto.getChapitreId());
                });
        
        logger.info("✅ Chapitre trouvé: {} (ID: {})", chapitre.getNom(), chapitre.getId());
        
        // ======================
        // 3. CRÉER LE QUESTIONNAIRE
        // ======================
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitre(dto.getTitre());
        questionnaire.setDescription(dto.getDescription());
        questionnaire.setDuree(dto.getDuree());
        questionnaire.setChapitre(chapitre);
        
        // Auteur par défaut
        if (questionnaire.getAuteur() == null) {
            questionnaire.setAuteur("Système");
        }
        
        // ======================
        // 4. ⚡ AJOUTER LES QUESTIONS (PARTIE CRITIQUE)
        // ======================
        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            logger.info("📝 Traitement de {} questions:", dto.getQuestions().size());
            
            int questionIndex = 0;
            for (QuestionDto questionDto : dto.getQuestions()) {
                questionIndex++;
                logger.info("  📋 Question {}/{}: '{}' (Type: {})", 
                    questionIndex, dto.getQuestions().size(), 
                    questionDto.getEnonce(), questionDto.getType());
                
                // Créer l'entité Question
                Question question = new Question();
                question.setEnonce(questionDto.getEnonce());
                question.setTypeQuestion(questionDto.getType());
                question.setPoints(questionDto.getPoints());
                question.setQuestionnaire(questionnaire); // ⚡ LIEN BIDIRECTIONNEL
                
                // ======================
                // 5. AJOUTER LES RÉPONSES
                // ======================
                if (questionDto.getReponses() != null && !questionDto.getReponses().isEmpty()) {
                    logger.info("    📋 {} réponse(s) pour cette question", questionDto.getReponses().size());
                    
                    int reponseIndex = 0;
                    int correctesCount = 0;
                    
                    for (ReponsePourQuestionDto reponseDto : questionDto.getReponses()) {
                        reponseIndex++;
                        
                        Reponse reponse = new Reponse();
                        reponse.setTexte(reponseDto.getTexte());
                        reponse.setCorrecte(reponseDto.isCorrecte());
                        reponse.setQuestion(question); // ⚡ LIEN BIDIRECTIONNEL
                        
                        if (reponseDto.isCorrecte()) {
                            correctesCount++;
                        }
                        
                        question.getReponses().add(reponse);
                        
                        logger.debug("      - Réponse {}: '{}' (Correcte: {})", 
                            reponseIndex, reponseDto.getTexte(), reponseDto.isCorrecte());
                    }
                    
                    logger.info("    ✅ {} réponse(s) correcte(s) sur {}", correctesCount, reponseIndex);
                } else {
                    logger.info("    ⚠️ Aucune réponse pour cette question");
                }
                
                // Ajouter la question au questionnaire
                questionnaire.getQuestions().add(question);
                logger.info("    ➕ Question ajoutée au questionnaire");
            }
            
            logger.info("✅ Total: {} questions traitées", dto.getQuestions().size());
        } else {
            logger.warn("⚠️ Aucune question fournie dans le DTO");
        }
        
        // ======================
        // 6. SAUVEGARDER (CASCADE)
        // ======================
        logger.info("💾 Sauvegarde du questionnaire en base...");
        Questionnaire saved = questionnaireRepository.save(questionnaire);
        
        // ======================
        // 7. LOGS DE CONFIRMATION
        // ======================
        logger.info("=== ✅ QUESTIONNAIRE CRÉÉ AVEC SUCCÈS ===");
        logger.info("📌 ID: {}", saved.getId());
        logger.info("📌 Titre: {}", saved.getTitre());
        logger.info("📌 Chapitre: {} (ID: {})", 
            saved.getChapitre().getNom(), saved.getChapitre().getId());
        logger.info("📌 Questions sauvegardées: {}", 
            saved.getQuestions() != null ? saved.getQuestions().size() : 0);
        
        // Vérification détaillée des questions sauvegardées
        if (saved.getQuestions() != null) {
            logger.info("📊 Détail des questions sauvegardées:");
            saved.getQuestions().forEach(q -> {
                logger.info("  - Question ID {}: '{}' ({} réponse(s))", 
                    q.getId(), q.getEnonce(), 
                    q.getReponses() != null ? q.getReponses().size() : 0);
            });
        }
        
        // ======================
        // 8. RETOURNER LE DTO
        // ======================
        QuestionnaireDetailDto resultDto = new QuestionnaireDetailDto(saved);
        logger.info("📤 Retour du DTO avec {} questions", 
            resultDto.getQuestions() != null ? resultDto.getQuestions().size() : 0);
        
        return resultDto;
    }

    // ====================================================================
    // --- Mettre à jour un questionnaire existant ---
    // ====================================================================
    @Transactional
    public QuestionnaireDetailDto updateQuestionnaire(Long id, QuestionnaireDetailDto dto) {
        logger.info("Service: Mise à jour du questionnaire ID: {}", id);
        
        Questionnaire existing = questionnaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionnaire non trouvé avec l'ID: " + id));
        
        // Mise à jour des champs
        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setDuree(dto.getDuree());
        
        // Mise à jour du chapitre si fourni
        if (dto.getChapitreId() != null) {
            Chapitre chapitre = chapitreRepository.findById(dto.getChapitreId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapitre non trouvé"));
            existing.setChapitre(chapitre);
        }
        
        logger.info("✅ Questionnaire ID {} mis à jour", id);
        return new QuestionnaireDetailDto(existing);
    }

    // ====================================================================
    // --- Supprimer un questionnaire ---
    // ====================================================================
    @Transactional
    public boolean deleteQuestionnaire(Long id) {
        logger.info("Service: Suppression du questionnaire ID: {}", id);
        
        if (!questionnaireRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un questionnaire inexistant ID: {}", id);
            return false;
        }
        
        questionnaireRepository.deleteById(id);
        logger.info("✅ Questionnaire ID {} supprimé", id);
        return true;
    }
    
    // ====================================================================
    // --- Méthodes utilitaires privées ---
    // ====================================================================
    
    private QuestionnaireDetailDto convertToDtoWithQuestions(Questionnaire questionnaire) {
        return questionnaireRepository
                .findByIdWithQuestions(questionnaire.getId())
                .map(QuestionnaireDetailDto::new)
                .orElseGet(() -> {
                    logger.warn("Impossible de charger les questions pour le questionnaire ID: {}", 
                        questionnaire.getId());
                    return new QuestionnaireDetailDto(questionnaire);
                });
    }
    
    // ====================================================================
    // --- Méthode utilitaire pour debug ---
    // ====================================================================
    
    /**
     * Méthode pour debuguer les données reçues
     */
    public void debugDTO(QuestionnaireDetailDto dto) {
        logger.info("=== 🔍 DEBUG DTO ===");
        logger.info("Titre: {}", dto.getTitre());
        logger.info("ChapitreId: {}", dto.getChapitreId());
        logger.info("Nombre de questions: {}", 
            dto.getQuestions() != null ? dto.getQuestions().size() : "null");
        
        if (dto.getQuestions() != null) {
            for (int i = 0; i < dto.getQuestions().size(); i++) {
                QuestionDto q = dto.getQuestions().get(i);
                logger.info("  Question {}: {}", i + 1, q.getEnonce());
                logger.info("    Type: {}, Points: {}", q.getType(), q.getPoints());
                logger.info("    Réponses: {}", 
                    q.getReponses() != null ? q.getReponses().size() : 0);
            }
        }
        logger.info("=== FIN DEBUG ===");
    }
}