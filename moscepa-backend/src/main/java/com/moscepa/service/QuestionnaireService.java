package com.moscepa.service;

import com.moscepa.dto.GenerateurPayload;
import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionnairePayload;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Question;
import com.moscepa.entity.Questionnaire;
import com.moscepa.entity.Reponse;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.QuestionnaireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireService.class);

    private final QuestionnaireRepository questionnaireRepository;
    private final ChapitreRepository chapitreRepository;
    private final TestService testService; // ✅ injecter TestService

    public QuestionnaireService(QuestionnaireRepository questionnaireRepository,
                                ChapitreRepository chapitreRepository,
                                TestService testService) { // ✅ remplacer TestRepository par TestService
        this.questionnaireRepository = questionnaireRepository;
        this.chapitreRepository = chapitreRepository;
        this.testService = testService;
    }

    // ====================================================================
    // === CRÉATION / SAUVEGARDE DU QUESTIONNAIRE (brouillon)           ===
    // ====================================================================
    @Transactional
    public Questionnaire sauvegarderQuestionnaire(QuestionnairePayload payload) {
        log.info("Requête reçue pour CRÉER un questionnaire : {}", payload.getTitre());

        Chapitre chapitre = chapitreRepository.findById(payload.getChapitreId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Chapitre non trouvé avec l'ID: " + payload.getChapitreId()));

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitre(payload.getTitre());
        questionnaire.setDescription(payload.getDescription());
        questionnaire.setChapitre(chapitre);
        questionnaire.setDateCreation(LocalDateTime.now());

        List<Question> questions = new ArrayList<>();
        if (payload.getQuestions() != null) {
            payload.getQuestions().forEach(qDto -> {
                Question question = new Question();
                question.setEnonce(qDto.getEnonce());
                question.setTypeQuestion(qDto.getType());
                question.setPoints(qDto.getPoints());
                question.setQuestionnaire(questionnaire);

                List<Reponse> reponses = new ArrayList<>();
                if (qDto.getReponses() != null) {
                    qDto.getReponses().forEach(rDto -> {
                        Reponse reponse = new Reponse();
                        reponse.setTexte(rDto.getTexte());
                        reponse.setCorrecte(rDto.isCorrecte());
                        reponse.setQuestion(question);
                        reponses.add(reponse);
                    });
                }
                question.setReponses(reponses);
                questions.add(question);
            });
        }

        questionnaire.setQuestions(questions);

        Questionnaire questionnaireSauvegarde = questionnaireRepository.save(questionnaire);
        log.info("Questionnaire '{}' et ses {} questions ont été sauvegardés avec succès.",
                questionnaireSauvegarde.getTitre(),
                questionnaireSauvegarde.getQuestions() != null ? questionnaireSauvegarde.getQuestions().size() : 0);

        // ✅ Créer automatiquement un test lié au questionnaire
        testService.creerTestDepuisQuestionnaire(questionnaireSauvegarde.getId());

        return questionnaireSauvegarde;
    }

    // ====================================================================
    // === LISTE DES QUESTIONNAIRES (DTO)                                ===
    // ====================================================================
    @Transactional(readOnly = true)
    public List<QuestionnaireDetailDto> findAllQuestionnaires() {
        return questionnaireRepository.findAll()
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Questionnaire> findAll() {
        return questionnaireRepository.findAll();
    }

    // ====================================================================
    // === GÉNÉRATION AUTOMATIQUE DE QUESTIONNAIRE                       ===
    // ====================================================================
    @Transactional
    public QuestionnaireDetailDto genererQuestionnaireDepuisBanque(GenerateurPayload params) {
        log.info("Génération automatique de questionnaire: {}", params.getTitre());

        Questionnaire q = new Questionnaire();
        q.setTitre(params.getTitre());
        q.setDescription("Questionnaire généré automatiquement");
        q.setDateCreation(LocalDateTime.now());

        if (params.getChapitreId() != null) {
            Chapitre chapitre = chapitreRepository.findById(params.getChapitreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Chapitre non trouvé avec l'ID: " + params.getChapitreId()));
            q.setChapitre(chapitre);
        }

        Questionnaire saved = questionnaireRepository.save(q);

        // ✅ Créer automatiquement un test lié au questionnaire généré
        testService.creerTestDepuisQuestionnaire(saved.getId());

        return toDetailDto(saved);
    }

    // ====================================================================
    // === SUPPRESSION                                                   ===
    // ====================================================================
    @Transactional
    public void deleteQuestionnaireById(Long id) {
        log.info("Suppression du questionnaire ID: {}", id);
        if (!questionnaireRepository.existsById(id)) {
            throw new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + id);
        }
        questionnaireRepository.deleteById(id);
    }

    // ====================================================================
    // === MAPPERS                                                       ===
    // ====================================================================
    private QuestionnaireDetailDto toDetailDto(Questionnaire q) {
        Long chapitreId = q.getChapitre() != null ? q.getChapitre().getId() : null;
        return new QuestionnaireDetailDto(
                q.getId(),
                q.getTitre(),
                q.getDescription(),
                q.getDateCreation(),
                chapitreId
        );
    }
}
