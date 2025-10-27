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
    private final EtudiantRepository etudiantRepository;
    
    public TestService(TestRepository testRepository, QuestionRepository questionRepository, ParcoursService parcoursService, ResultatTestRepository resultatTestRepository, EtudiantRepository etudiantRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.parcoursService = parcoursService;
        this.resultatTestRepository = resultatTestRepository;
        this.etudiantRepository = etudiantRepository;
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

        // Le calcul est correct.
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
        // ===> SAUVEGARDE DU VRAI RÉSULTAT DANS LA BASE DE DONNÉES <===
        // ====================================================================
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun profil étudiant trouvé pour l'utilisateur ID: " + utilisateurId));
        
        Test test = testRepository.findTopByChapitreId(chapitreId)
                .orElseThrow(() -> new EntityNotFoundException("Aucun test trouvé pour le chapitre ID: " + chapitreId));

        // Création de l'entité ResultatTest avec TOUS les champs requis
        ResultatTest nouveauResultat = new ResultatTest();
        nouveauResultat.setEtudiant(etudiant);
        nouveauResultat.setTest(test);
        nouveauResultat.setScore(scoreObtenu);
        nouveauResultat.setScoreTotal(totalPointsPossible);
        nouveauResultat.setDateTest(LocalDateTime.now());
        nouveauResultat.setBonnesReponses(nombreDeBonnesReponses);
        
        // ====================================================================
        // ===> LA DERNIÈRE CORRECTION EST ICI <===
        // ====================================================================
        // On fournit la valeur pour le champ 'total_questions' qui posait problème.
        nouveauResultat.setTotalQuestions(questionsDuTest.size());

        resultatTestRepository.save(nouveauResultat);
        System.out.println("[TestService] Le résultat du test (" + scoreObtenu + "/" + totalPointsPossible + ") a été sauvegardé pour l'étudiant ID " + etudiant.getId());


        // La logique de recommandation ne change pas et est correcte.
        double pourcentage = (totalPointsPossible > 0) ? (scoreObtenu / totalPointsPossible) * 100 : 0;
        System.out.println("[TestService] VÉRIFICATION DU POURCENTAGE: " + pourcentage + " < 33.0 ?");
        if (pourcentage < 33.0) {
            System.out.println("[TestService] Score < 33%. Appel de enregistrerParcoursRecommande.");
            parcoursService.enregistrerParcoursRecommande(utilisateurId, List.of(chapitreId));
        }

        // Le retour du DTO est correct.
        ResultatTestDto resultatDto = new ResultatTestDto();
        resultatDto.setChapitreId(chapitreId);
        resultatDto.setScoreObtenu(scoreObtenu);
        resultatDto.setTotalPointsPossible(totalPointsPossible);
        resultatDto.setDateSoumission(LocalDateTime.now());
        return resultatDto;
    }

    private boolean verifierReponse(Question question, Object reponseDonnee) {
        // Cette méthode est correcte et gère bien tous les cas.
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
