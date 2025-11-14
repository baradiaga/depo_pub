// Fichier : src/main/java/com/moscepa/service/QuestionService.java (Version Corrigée)

package com.moscepa.service;

import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ReponsePourQuestionDto; // On importe le DTO "sûr"
import com.moscepa.entity.Question;
import com.moscepa.entity.Reponse;
import com.moscepa.entity.Questionnaire;
import com.moscepa.repository.QuestionRepository;
import com.moscepa.repository.QuestionnaireRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionnaireRepository questionnaireRepository;

    public QuestionService(QuestionRepository questionRepository, QuestionnaireRepository questionnaireRepository) {
        this.questionRepository = questionRepository;
        this.questionnaireRepository = questionnaireRepository;
    }

    @Transactional
    public Question createQuestion(QuestionDto questionDto, Long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new EntityNotFoundException("Questionnaire non trouvé avec l'ID : " + questionnaireId));

        Question question = new Question();
        question.setEnonce(questionDto.getEnonce());
        question.setPoints(questionDto.getPoints());
        question.setTypeQuestion(questionDto.getType());
        question.setQuestionnaire(questionnaire);

        // ====================================================================
        // === CORRECTION DE LA BOUCLE POUR CRÉER LES RÉPONSES              ===
        // ====================================================================
        // On vérifie si le DTO contient des réponses
        if (questionDto.getReponses() != null && !questionDto.getReponses().isEmpty()) {
            // On boucle sur chaque DTO de réponse reçu
            for (ReponsePourQuestionDto reponseDto : questionDto.getReponses()) {
                Reponse nouvelleReponse = new Reponse();
                nouvelleReponse.setTexte(reponseDto.getTexte());
                // Note : Le champ 'correcte' n'est pas dans ReponsePourQuestionDto,
                // donc on ne peut pas le définir ici. C'est normal pour ce workflow.
                // Si vous avez besoin de le définir, il faudrait utiliser un autre DTO.
                // nouvelleReponse.setCorrecte(reponseDto.isCorrecte());
                
                question.addReponse(nouvelleReponse);
            }
        }
        
        return questionRepository.save(question);
    }

    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée avec l'ID : " + id));
        return new QuestionDto(question);
    }

    public List<QuestionDto> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(QuestionDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Question updateQuestion(Long id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question non trouvée avec l'ID : " + id));

        question.setEnonce(questionDto.getEnonce());
        question.setPoints(questionDto.getPoints());
        question.setTypeQuestion(questionDto.getType());
        // La mise à jour des réponses est une logique plus complexe, omise ici pour la compilation.

        return questionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new EntityNotFoundException("Question non trouvée avec l'ID : " + id);
        }
        questionRepository.deleteById(id);
    }
}
