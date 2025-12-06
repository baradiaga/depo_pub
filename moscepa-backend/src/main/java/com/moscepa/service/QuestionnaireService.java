package com.moscepa.service;

import com.moscepa.dto.GenerateurPayload;
import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionnairePayload;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private static final Logger log = LoggerFactory.getLogger(QuestionnaireService.class);

    private final QuestionnaireRepository questionnaireRepository;
    private final ChapitreRepository chapitreRepository;
    private final BanqueQuestionRepository banqueQuestionRepository; 
    private final TestService testService;

    public QuestionnaireService(QuestionnaireRepository questionnaireRepository,
                                ChapitreRepository chapitreRepository,
                                BanqueQuestionRepository banqueQuestionRepository, 
                                TestService testService) {
        this.questionnaireRepository = questionnaireRepository;
        this.chapitreRepository = chapitreRepository;
        this.banqueQuestionRepository = banqueQuestionRepository; 
        this.testService = testService;
    }

    // ====================================================================
    // === CRÉATION / SAUVEGARDE DU QUESTIONNAIRE (manuel)               ===
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
                question.setChapitre(chapitre);

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

        testService.creerTestDepuisQuestionnaire(questionnaireSauvegarde.getId());

        return questionnaireSauvegarde;
    }
    // ====================================================================
// === MISE À JOUR DU QUESTIONNAIRE                                    ===
// ====================================================================
@Transactional
public Questionnaire updateQuestionnaire(Long id, QuestionnairePayload payload) {
    log.info("Requête reçue pour MODIFIER le questionnaire ID: {}", id);

    Questionnaire existingQuestionnaire = questionnaireRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + id));

    Chapitre chapitre = chapitreRepository.findById(payload.getChapitreId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Chapitre non trouvé avec l'ID: " + payload.getChapitreId()));

    existingQuestionnaire.setTitre(payload.getTitre());
    existingQuestionnaire.setDescription(payload.getDescription());
    existingQuestionnaire.setChapitre(chapitre);
    // Note: La date de création n'est pas modifiée

    // Logique de mise à jour des questions et réponses
    // Pour simplifier, on supprime les anciennes questions et on ajoute les nouvelles
    // Dans un vrai projet, il faudrait gérer la mise à jour fine, mais pour un CRUD simple, c'est suffisant.
    existingQuestionnaire.getQuestions().clear();
    
    List<Question> newQuestions = new ArrayList<>();
    if (payload.getQuestions() != null) {
        payload.getQuestions().forEach(qDto -> {
            Question question = new Question();
            question.setEnonce(qDto.getEnonce());
            question.setTypeQuestion(qDto.getType());
            question.setPoints(qDto.getPoints());
            question.setQuestionnaire(existingQuestionnaire);
            question.setChapitre(chapitre); // Le chapitre est le même que celui du questionnaire

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
            newQuestions.add(question);
        });
    }
    existingQuestionnaire.setQuestions(newQuestions);

    Questionnaire questionnaireSauvegarde = questionnaireRepository.save(existingQuestionnaire);
    log.info("Questionnaire '{}' (ID: {}) a été mis à jour avec succès.",
            questionnaireSauvegarde.getTitre(),
            questionnaireSauvegarde.getId());

    // On ne recrée pas de test ici, on suppose que la mise à jour du questionnaire n'affecte pas les tests existants.
    // Si un nouveau test est nécessaire, il devra être créé manuellement.

    return questionnaireSauvegarde;
}


    // ====================================================================
    // === LISTE DES QUESTIONNAIRES                                      ===
    // ====================================================================
    @Transactional(readOnly = true)
    public List<QuestionnaireDetailDto> findAllQuestionnaires() {
        return questionnaireRepository.findAll()
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    // ====================================================================
    // === GÉNÉRATION AUTOMATIQUE DE QUESTIONNAIRE                       ===
    // ====================================================================
    @Transactional
    public QuestionnaireDetailDto genererQuestionnaireDepuisBanque(GenerateurPayload params) {
        log.info("Génération automatique de questionnaire: {}", params.getTitre());

        if ((params.getChapitreId() == null) &&
            (params.getChapitresIds() == null || params.getChapitresIds().isEmpty())) {
            throw new IllegalArgumentException("Vous devez sélectionner au moins un chapitre.");
        }

        Questionnaire q = new Questionnaire();
        q.setTitre(params.getTitre());
        q.setDescription("Questionnaire généré automatiquement");
        q.setDateCreation(LocalDateTime.now());

        List<BanqueQuestion> questionsDisponibles;

        if (params.getChapitreId() != null) {
            Chapitre chapitre = chapitreRepository.findById(params.getChapitreId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Chapitre non trouvé avec l'ID: " + params.getChapitreId()));
            q.setChapitre(chapitre);
            questionsDisponibles = banqueQuestionRepository.findByChapitre(chapitre);
        } else {
            questionsDisponibles = banqueQuestionRepository.findByChapitreIdIn(params.getChapitresIds());
        }

        if (questionsDisponibles.size() < params.getNombreQuestions()) {
            throw new IllegalArgumentException("Pas assez de questions disponibles pour générer le questionnaire.");
        }

        Collections.shuffle(questionsDisponibles);
        List<BanqueQuestion> selection = questionsDisponibles.subList(0, params.getNombreQuestions());

        List<Question> questions = selection.stream().map(bq -> {
            Question qEntity = new Question();
            qEntity.setEnonce(bq.getEnonce());
            qEntity.setTypeQuestion(bq.getTypeQuestion());
            qEntity.setPoints(bq.getPoints());
            qEntity.setQuestionnaire(q);

            List<Reponse> reponses = bq.getReponses().stream().map(br -> {
                Reponse r = new Reponse();
                r.setTexte(br.getTexte());
                r.setCorrecte(br.getCorrecte());
                r.setQuestion(qEntity);
                return r;
            }).collect(Collectors.toList());

            qEntity.setReponses(reponses);
            return qEntity;
        }).collect(Collectors.toList());

        q.setQuestions(questions);

        Questionnaire saved = questionnaireRepository.save(q);

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
    // === MAPPER DTO                                                    ===
    // ====================================================================
   private QuestionnaireDetailDto toDetailDto(Questionnaire q) {
    Long chapitreId = q.getChapitre() != null ? q.getChapitre().getId() : null;
    String nomChapitre = q.getChapitre() != null ? q.getChapitre().getNom() : null;
    String nomMatiere = q.getChapitre() != null && q.getChapitre().getElementConstitutif() != null
            ? q.getChapitre().getElementConstitutif().getNom()
            : null;
    int nombreQuestions = q.getQuestions() != null ? q.getQuestions().size() : 0;
    Integer duree = q.getDuree(); // si tu veux inclure la durée du questionnaire

    return new QuestionnaireDetailDto(
            q.getId(),
            q.getTitre(),
            q.getDescription(),
            q.getDateCreation(),
            chapitreId,
            nomChapitre,
            nomMatiere,
            nombreQuestions,
            duree
    );
}


    // ====================================================================
// === TESTS ASSOCIÉS AU QUESTIONNAIRE                               ===
// ====================================================================
@Transactional(readOnly = true)
public List<Test> getTestsByQuestionnaire(Long questionnaireId) {
    Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + questionnaireId));
    return testService.findByQuestionnaireId(questionnaireId);
}

@Transactional
public Test createTest(Long questionnaireId, Test test) {
    Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + questionnaireId));
    test.setQuestionnaire(questionnaire);
    return testService.save(test);
}

@Transactional
public Test updateTest(Long testId, Test testData) {
    return testService.update(testId, testData);
}

@Transactional
public void deleteTest(Long testId) {
    testService.deleteById(testId);
}
@Transactional(readOnly = true)
public QuestionnaireDetailDto findDetailById(Long id) {
    Questionnaire q = questionnaireRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID: " + id));
    return toDetailDto(q);
}

}
