package com.moscepa.controller;

import com.moscepa.dto.QuestionDto;
import com.moscepa.entity.Question;
import com.moscepa.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions" )
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/{questionnaireId}")
    public ResponseEntity<Question> createQuestion(@Valid @RequestBody QuestionDto questionDto, @PathVariable Long questionnaireId) {
        Question createdQuestion = questionService.createQuestion(questionDto, questionnaireId);
        return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        QuestionDto questionDto = questionService.getQuestionById(id);
        return ResponseEntity.ok(questionDto);
    }

    @GetMapping
    public ResponseEntity<List<QuestionDto>> getAllQuestions() {
        List<QuestionDto> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @Valid @RequestBody QuestionDto questionDto) {
        Question updatedQuestion = questionService.updateQuestion(id, questionDto);
        return ResponseEntity.ok(updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
}
