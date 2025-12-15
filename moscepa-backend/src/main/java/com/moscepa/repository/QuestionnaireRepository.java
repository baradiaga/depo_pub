package com.moscepa.repository;

import com.moscepa.entity.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    
    // Méthode standard (LAZY - ne charge pas les questions)
    // Optional<Questionnaire> findById(Long id); // Déjà fourni par JpaRepository
    
    // 🔥 NOUVELLE MÉTHODE : Charge le questionnaire AVEC ses questions
    @Query("SELECT DISTINCT q FROM Questionnaire q " +
           "LEFT JOIN FETCH q.questions " +  // Force le chargement des questions
           "WHERE q.id = :id")
    Optional<Questionnaire> findByIdWithQuestions(@Param("id") Long id);
    
    // Optionnel : pour charger aussi les réponses des questions
    @Query("SELECT DISTINCT q FROM Questionnaire q " +
           "LEFT JOIN FETCH q.questions quest " +
           "LEFT JOIN FETCH quest.reponses " +  // Si vous avez besoin des réponses
           "WHERE q.id = :id")
    Optional<Questionnaire> findByIdWithQuestionsAndReponses(@Param("id") Long id);
}