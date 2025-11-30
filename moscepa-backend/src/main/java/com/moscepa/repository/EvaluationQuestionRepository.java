// Fichier : src/main/java/com/moscepa/repository/EvaluationQuestionRepository.java

package com.moscepa.repository;

import com.moscepa.entity.EvaluationQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationQuestionRepository extends JpaRepository<EvaluationQuestion, Long> {

    // Méthode pour trouver une évaluation spécifique par question et par évaluateur
    Optional<EvaluationQuestion> findByBanqueQuestionIdAndEvaluateurId(Long questionId, Long evaluateurId);
}
