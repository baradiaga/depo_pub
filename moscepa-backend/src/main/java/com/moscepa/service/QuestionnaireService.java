// Fichier : QuestionnaireService.java (Version Corrigée)

package com.moscepa.service;

import com.moscepa.dto.GenerateurPayloadDTO;
import com.moscepa.dto.QuestionnaireDetailDto;
import com.moscepa.dto.QuestionnairePayloadDTO;
import com.moscepa.entity.*;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.QuestionRepository;
import com.moscepa.repository.QuestionnaireRepository;
import com.moscepa.repository.TestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final QuestionRepository questionRepository;
    private final ChapitreRepository chapitreRepository;
    private final TestRepository testRepository;

    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, QuestionRepository questionRepository, ChapitreRepository chapitreRepository, TestRepository testRepository ) {
        this.questionnaireRepository = questionnaireRepository;
        this.questionRepository = questionRepository;
        this.chapitreRepository = chapitreRepository;
        this.testRepository = testRepository;
    }

    @Transactional
    public void sauvegarderQuestionnaire(QuestionnairePayloadDTO payload) {
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setTitre(payload.getTitre());
        questionnaire.setDescription(payload.getDescription());
        questionnaire.setDuree(payload.getDuree());

        Chapitre chapitre = chapitreRepository.findById(payload.getChapitreId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapitre non trouvé avec l'id: " + payload.getChapitreId()));
        questionnaire.setChapitre(chapitre);

        if (payload.getQuestions() != null) {
            payload.getQuestions().forEach(questionDto -> {
                Question question = new Question();
                question.setEnonce(questionDto.getEnonce());
                question.setTypeQuestion(questionDto.getType());
                question.setPoints(questionDto.getPoints());
                question.setReponseCorrecteTexte(questionDto.getReponseCorrecteTexte());

                if (questionDto.getReponses() != null) {
                    questionDto.getReponses().forEach(reponseDto -> {
                        Reponse reponse = new Reponse();
                        reponse.setTexte(reponseDto.getTexte());
                        reponse.setCorrecte(reponseDto.isCorrecte());
                        question.addReponse(reponse);
                    });
                }
                questionnaire.addQuestion(question);
            });
        }

        boolean testExiste = testRepository.existsByChapitreId(payload.getChapitreId());
        if (!testExiste) {
            System.out.println("[QuestionnaireService] Aucun Test trouvé pour le chapitre ID " + payload.getChapitreId() + ". Création d'un Test par défaut.");
            Test nouveauTest = new Test();
            nouveauTest.setTitre("Évaluation - " + chapitre.getNom());
            nouveauTest.setChapitre(chapitre);
            testRepository.save(nouveauTest);
        } else {
            System.out.println("[QuestionnaireService] Un Test existe déjà pour le chapitre ID " + payload.getChapitreId() + ". Aucune action nécessaire.");
        }

        questionnaireRepository.save(questionnaire);
    }

    @Transactional
    public QuestionnaireDetailDto genererQuestionnaireDepuisBanque(GenerateurPayloadDTO payload) {
        List<Question> questionsDisponibles = questionRepository.findBanqueQuestionsByChapitresIds(payload.getChapitresIds());

        if (questionsDisponibles.size() < payload.getNombreQuestions()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas assez de questions dans la banque...");
        }

        Collections.shuffle(questionsDisponibles);
        List<Question> questionsSelectionnees = questionsDisponibles.subList(0, payload.getNombreQuestions());

        Questionnaire nouveauQuestionnaire = new Questionnaire();
        nouveauQuestionnaire.setTitre(payload.getTitre());
        nouveauQuestionnaire.setDuree(payload.getDuree());

        Chapitre premierChapitre = chapitreRepository.findById(payload.getChapitresIds().get(0))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chapitre non trouvé."));
        nouveauQuestionnaire.setChapitre(premierChapitre);

        for (Question questionOriginale : questionsSelectionnees) {
            Question questionClone = new Question();
            questionClone.setEnonce(questionOriginale.getEnonce());
            questionClone.setTypeQuestion(questionOriginale.getTypeQuestion());
            questionClone.setPoints(questionOriginale.getPoints());
            questionClone.setReponseCorrecteTexte(questionOriginale.getReponseCorrecteTexte());
            questionClone.setQuestionnaire(nouveauQuestionnaire);

            for (Reponse reponseOriginale : questionOriginale.getReponses()) {
                Reponse reponseClone = new Reponse();
                reponseClone.setTexte(reponseOriginale.getTexte());
                reponseClone.setCorrecte(reponseOriginale.isCorrecte());
                questionClone.addReponse(reponseClone);
            }
            nouveauQuestionnaire.addQuestion(questionClone);
        }

        boolean testExiste = testRepository.existsByChapitreId(premierChapitre.getId());
        if (!testExiste) {
            System.out.println("[QuestionnaireService] Aucun Test trouvé pour le chapitre ID " + premierChapitre.getId() + ". Création d'un Test par défaut.");
            Test nouveauTest = new Test();
            nouveauTest.setTitre("Évaluation - " + premierChapitre.getNom());
            nouveauTest.setChapitre(premierChapitre);
            testRepository.save(nouveauTest);
        }

        Questionnaire questionnaireSauvegarde = questionnaireRepository.save(nouveauQuestionnaire);
        return convertToDetailDto(questionnaireSauvegarde);
    }

    private QuestionnaireDetailDto convertToDetailDto(Questionnaire questionnaire) {
        QuestionnaireDetailDto dto = new QuestionnaireDetailDto();
        dto.setId(questionnaire.getId());
        dto.setTitre(questionnaire.getTitre());
        dto.setDuree(questionnaire.getDuree());
        dto.setDescription(questionnaire.getDescription());
        if (questionnaire.getQuestions() != null) {
            dto.setNombreQuestions(questionnaire.getQuestions().size());
        }

        if (questionnaire.getChapitre() != null) {
            dto.setNomChapitre(questionnaire.getChapitre().getNom());
            
            // ====================================================================
            // === CORRECTION APPLIQUÉE ICI                                     ===
            // ====================================================================
            // On utilise getElementConstitutif() au lieu de getMatiere()
            if (questionnaire.getChapitre().getElementConstitutif() != null) {
                dto.setNomMatiere(questionnaire.getChapitre().getElementConstitutif().getNom());
            }
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public List<QuestionnaireDetailDto> findAllQuestionnaires() {
        return questionnaireRepository.findAll().stream()
                .map(this::convertToDetailDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQuestionnaireById(Long id) {
        if (!questionnaireRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Questionnaire non trouvé avec l'ID : " + id);
        }
        questionnaireRepository.deleteById(id);
    }
}
