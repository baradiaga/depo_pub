package com.moscepa.service; // Assurez-vous que le package est correct

import com.moscepa.dto.QuestionBanqueDto;
import com.moscepa.entity.Question;
import com.moscepa.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BanqueQuestionService {

    private final QuestionRepository questionRepository;

    public BanqueQuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public List<QuestionBanqueDto> getBanqueQuestionsByChapitre(Long chapitreId) {
        List<Question> questions = questionRepository.findBanqueQuestionsByChapitreId(chapitreId);
        return questions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Méthode de conversion de l'entité Question vers le DTO
    private QuestionBanqueDto convertToDto(Question question) {
        QuestionBanqueDto dto = new QuestionBanqueDto();
        dto.setId(question.getId());
        dto.setEnonce(question.getEnonce());
        dto.setTypeQuestion(question.getTypeQuestion());

        List<QuestionBanqueDto.ReponseDto> reponseDtos = question.getReponses().stream().map(reponse -> {
            QuestionBanqueDto.ReponseDto reponseDto = new QuestionBanqueDto.ReponseDto();
            reponseDto.setId(reponse.getId());
            reponseDto.setTexte(reponse.getTexte());
            reponseDto.setCorrecte(reponse.isCorrecte());
            return reponseDto;
        }).collect(Collectors.toList());

        dto.setReponses(reponseDtos);
        return dto;
    }
}
