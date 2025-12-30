package com.moscepa.service;
import com.moscepa.entity.TypeQuestionnaire; 

import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ReponsePourQuestionDto;
import com.moscepa.dto.GenerationRequestDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Questionnaire;
import com.moscepa.entity.Question;
import com.moscepa.entity.Reponse;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.QuestionnaireRepository;
import com.moscepa.repository.QuestionRepository;
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
    
    @Autowired
    private QuestionRepository questionRepository;

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
        // Dans la section 1. VALIDATIONS
if (dto.getType() == null) {
    logger.error("❌ Type de questionnaire manquant dans le DTO");
    throw new IllegalArgumentException("Le type de questionnaire (EXERCICE, TEST, QUIZ) est obligatoire");
}

        // ======================
        // 3. CRÉER LE QUESTIONNAIRE
        // ======================
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitre(dto.getTitre());
        questionnaire.setDescription(dto.getDescription());
        questionnaire.setDuree(dto.getDuree());
        questionnaire.setChapitre(chapitre);
        questionnaire.setType(dto.getType()); 
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
    // --- Générer un questionnaire depuis la banque de questions ---
    // ====================================================================
    @Transactional
    public QuestionnaireDetailDto genererQuestionnaireDepuisBanque(GenerationRequestDto generationRequest) {
        logger.info("=== 🚀 DÉBUT GÉNÉRATION QUESTIONNAIRE DEPUIS BANQUE ===");
        logger.info("📋 Paramètres de génération:");
        logger.info("  - Thèmes: {}", generationRequest.getThemes());
        logger.info("  - Nombre de questions: {}", generationRequest.getNombreQuestions());
        logger.info("  - Niveau: {}", generationRequest.getNiveau());
        
        // ======================
        // 1. VALIDATIONS
        // ======================
        if (generationRequest.getThemes() == null || generationRequest.getThemes().isEmpty()) {
            throw new IllegalArgumentException("Au moins un thème doit être spécifié");
        }
        
        if (generationRequest.getNombreQuestions() <= 0) {
            throw new IllegalArgumentException("Le nombre de questions doit être positif");
        }
        
        // ======================
        // 2. RÉCUPÉRER LES QUESTIONS DE LA BANQUE
        // ======================
        logger.info("🔍 Recherche des questions dans la banque...");
        List<Question> questionsDisponibles = questionRepository
                .findByThemesAndNiveau(generationRequest.getThemes(), generationRequest.getNiveau());
        
        logger.info("📊 Questions disponibles: {} question(s) trouvée(s)", questionsDisponibles.size());
        
        if (questionsDisponibles.isEmpty()) {
            throw new IllegalStateException("Aucune question trouvée pour les critères spécifiés");
        }
        
        // ======================
        // 3. SÉLECTIONNER LES QUESTIONS
        // ======================
        int nombreQuestions = Math.min(generationRequest.getNombreQuestions(), questionsDisponibles.size());
        List<Question> questionsSelectionnees = questionsDisponibles.stream()
                .limit(nombreQuestions)
                .collect(Collectors.toList());
        
        logger.info("✅ {} question(s) sélectionnée(s) pour le questionnaire", questionsSelectionnees.size());
        
        // ======================
        // 4. CRÉER LE QUESTIONNAIRE
        // ======================
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitre("Questionnaire généré - " + String.join(", ", generationRequest.getThemes()));
        questionnaire.setDescription("Questionnaire généré automatiquement à partir de la banque de questions");
        questionnaire.setDuree(60); // Durée par défaut
        questionnaire.setAuteur("Système");
        
        // Trouver un chapitre par défaut (premier chapitre disponible)
        Chapitre chapitreParDefaut = chapitreRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun chapitre disponible"));
        questionnaire.setChapitre(chapitreParDefaut);
        
        // ======================
        // 5. COPIER LES QUESTIONS SÉLECTIONNÉES
        // ======================
        logger.info("📝 Copie des questions sélectionnées...");
        for (Question questionSource : questionsSelectionnees) {
            Question nouvelleQuestion = new Question();
            nouvelleQuestion.setEnonce(questionSource.getEnonce());
            nouvelleQuestion.setTypeQuestion(questionSource.getTypeQuestion());
            nouvelleQuestion.setPoints(questionSource.getPoints());
            nouvelleQuestion.setQuestionnaire(questionnaire);
            
            // Copier les réponses
            for (Reponse reponseSource : questionSource.getReponses()) {
                Reponse nouvelleReponse = new Reponse();
                nouvelleReponse.setTexte(reponseSource.getTexte());
                nouvelleReponse.setCorrecte(reponseSource.isCorrecte());
                nouvelleReponse.setQuestion(nouvelleQuestion);
                nouvelleQuestion.getReponses().add(nouvelleReponse);
            }
            
            questionnaire.getQuestions().add(nouvelleQuestion);
        }
        
        // ======================
        // 6. SAUVEGARDER
        // ======================
        logger.info("💾 Sauvegarde du questionnaire généré...");
        Questionnaire saved = questionnaireRepository.save(questionnaire);
        
        // ======================
        // 7. LOGS FINAUX
        // ======================
        logger.info("=== ✅ QUESTIONNAIRE GÉNÉRÉ AVEC SUCCÈS ===");
        logger.info("📌 ID: {}", saved.getId());
        logger.info("📌 Titre: {}", saved.getTitre());
        logger.info("📌 Nombre de questions: {}", 
            saved.getQuestions() != null ? saved.getQuestions().size() : 0);
        
        return new QuestionnaireDetailDto(saved);
    }

    // ====================================================================
    // --- Mettre à jour un questionnaire existant ---
    // ====================================================================
        @Transactional
    public QuestionnaireDetailDto updateQuestionnaire(Long id, QuestionnaireDetailDto dto) {
        logger.info("Service: Mise à jour du questionnaire ID: {}", id);
        
        Questionnaire existing = questionnaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Questionnaire non trouvé avec l'ID: " + id));
        
        // 1. Mise à jour des champs (Ajout du TYPE)
        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setDuree(dto.getDuree());
        existing.setType(dto.getType()); // ⚡ INDISPENSABLE pour 2025
        
        // 2. Mise à jour du chapitre
        if (dto.getChapitreId() != null) {
            Chapitre chapitre = chapitreRepository.findById(dto.getChapitreId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapitre non trouvé"));
            existing.setChapitre(chapitre);
        }

        // 3. Mise à jour des questions (Pour éviter les doublons de questionnaires/questions)
        // On vide la liste existante pour forcer le remplacement par les nouvelles données du DTO
        existing.getQuestions().clear();
        
        if (dto.getQuestions() != null && !dto.getQuestions().isEmpty()) {
            for (QuestionDto qDto : dto.getQuestions()) {
                Question question = new Question();
                question.setEnonce(qDto.getEnonce());
                question.setTypeQuestion(qDto.getType());
                question.setPoints(qDto.getPoints());
                question.setQuestionnaire(existing); // ⚡ LIEN BIDIRECTIONNEL
                
                if (qDto.getReponses() != null) {
                    for (ReponsePourQuestionDto rDto : qDto.getReponses()) {
                        Reponse reponse = new Reponse();
                        reponse.setTexte(rDto.getTexte());
                        reponse.setCorrecte(rDto.isCorrecte());
                        reponse.setQuestion(question);
                        question.getReponses().add(reponse);
                    }
                }
                existing.getQuestions().add(question);
            }
        }
        
        // 4. Sauvegarde explicite
        Questionnaire saved = questionnaireRepository.save(existing);
        logger.info("✅ Questionnaire ID {} mis à jour avec son type et ses questions", id);
        return new QuestionnaireDetailDto(saved);
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