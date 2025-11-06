// Fichier : src/main/java/com/moscepa/service/TestService.java (Version Finale Corrigée)

package com.moscepa.service;

import com.moscepa.dto.QuestionDto;
import com.moscepa.dto.ResultatTestDto;
import com.moscepa.entity.*;
import com.moscepa.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final ParcoursService parcoursService;
    private final ResultatTestRepository resultatTestRepository;
    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI : On injecte UtilisateurRepository  ===
    // ====================================================================
    private final UtilisateurRepository utilisateurRepository;
    
    // On met à jour le constructeur
    public TestService(TestRepository testRepository, QuestionRepository questionRepository, ParcoursService parcoursService, ResultatTestRepository resultatTestRepository, UtilisateurRepository utilisateurRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.parcoursService = parcoursService;
        this.resultatTestRepository = resultatTestRepository;
        this.utilisateurRepository = utilisateurRepository; // <-- On l'assigne
    }

    @Transactional
    public ResultatTestDto calculerEtSauvegarderResultat(Long chapitreId, Long utilisateurId, Map<String, Object> reponsesUtilisateur) {
        
        System.out.println("\n\n**************************************************");
        System.out.println("*** [TestService] ID utilisateur REÇU en paramètre: " + utilisateurId + " ***");
        System.out.println("**************************************************\n\n");

        List<Question> questionsDuTest = questionRepository.findByQuestionnaireChapitreId(chapitreId);
        if (questionsDuTest.isEmpty()) {
            throw new EntityNotFoundException("Aucune question trouvée pour le chapitre ID : " + chapitreId);
        }

        double scoreObtenu = 0.0;
        double totalPointsPossible = 0.0;
        int nombreDeBonnesReponses = 0;
        for (Question question : questionsDuTest) {
            totalPointsPossible += question.getPoints();
            String questionIdStr = String.valueOf(question.getId());
            Object reponseDonnee = reponsesUtilisateur.get(questionIdStr);
            if (reponseDonnee != null && verifierReponse(question, reponseDonnee)) {
                scoreObtenu += question.getPoints();
                nombreDeBonnesReponses++;
            }
        }

        // ====================================================================
        // === CORRECTION APPLIQUÉE ICI : On utilise UtilisateurRepository  ===
        // ====================================================================
        Utilisateur etudiant = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur (étudiant) trouvé pour l'ID: " + utilisateurId));
        
        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));

        ResultatTest nouveauResultat = new ResultatTest();
        nouveauResultat.setEtudiant(etudiant); // <-- C'est maintenant un Utilisateur, c'est correct
        nouveauResultat.setTest(test);
        nouveauResultat.setScore(scoreObtenu);
        nouveauResultat.setScoreTotal(totalPointsPossible);
        nouveauResultat.setDateTest(LocalDateTime.now());
        nouveauResultat.setBonnesReponses(nombreDeBonnesReponses);
        nouveauResultat.setTotalQuestions(questionsDuTest.size());

        resultatTestRepository.save(nouveauResultat);
        System.out.println("[TestService] Le résultat du test (" + scoreObtenu + "/" + totalPointsPossible + ") a été sauvegardé pour l'étudiant ID " + etudiant.getId());

        double pourcentage = (totalPointsPossible > 0) ? (scoreObtenu / totalPointsPossible) * 100 : 0;
        System.out.println("[TestService] VÉRIFICATION DU POURCENTAGE: " + pourcentage + " < 33.0 ?");
        if (pourcentage < 33.0) {
            System.out.println("[TestService] Score < 33%. Appel de enregistrerParcoursRecommande.");
            parcoursService.enregistrerParcoursRecommande(utilisateurId, List.of(chapitreId));
        }

        ResultatTestDto resultatDto = new ResultatTestDto();
        resultatDto.setChapitreId(chapitreId);
        resultatDto.setScoreObtenu(scoreObtenu);
        resultatDto.setTotalPointsPossible(totalPointsPossible);
        resultatDto.setDateSoumission(LocalDateTime.now());
        return resultatDto;
    }

    private boolean verifierReponse(Question question, Object reponseDonnee) {
        switch (question.getTypeQuestion()) {
            case QCU:
                Long reponseIdQCU = ((Number) reponseDonnee).longValue();
                return question.getReponses().stream().anyMatch(r -> r.getId().equals(reponseIdQCU) && r.isCorrecte());
            case VRAI_FAUX:
                boolean reponseBool = Boolean.parseBoolean(reponseDonnee.toString());
                return String.valueOf(reponseBool).equalsIgnoreCase(question.getReponseCorrecteTexte());
            case TEXTE_LIBRE:
                String reponseTexte = (String) reponseDonnee;
                return reponseTexte.equalsIgnoreCase(question.getReponseCorrecteTexte());
            case QCM:
                if (!(reponseDonnee instanceof List)) return false;
                List<Long> reponsesIdsQCM = ((List<?>) reponseDonnee).stream().map(item -> ((Number) item).longValue()).collect(Collectors.toList());
                List<Long> bonnesReponsesIds = question.getReponses().stream().filter(Reponse::isCorrecte).map(Reponse::getId).collect(Collectors.toList());
                return reponsesIdsQCM.size() == bonnesReponsesIds.size() && bonnesReponsesIds.containsAll(reponsesIdsQCM);
            default:
                return false;
        }
    }

    public List<QuestionDto> getQuestionsByChapitre(Long chapitreId) {
        List<Question> questions = questionRepository.findByQuestionnaireChapitreId(chapitreId);
        return questions.stream().map(QuestionDto::new).collect(Collectors.toList());
    }
}
