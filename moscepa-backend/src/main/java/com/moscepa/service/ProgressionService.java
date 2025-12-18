package com.moscepa.service;

import com.moscepa.dto.ChapitreProgressDto;
import com.moscepa.dto.MatiereStatutDto;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.ElementConstitutif;
import com.moscepa.entity.ResultatTest;
import com.moscepa.repository.ElementConstitutifRepository;
import com.moscepa.repository.ResultatTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressionService {

   private final ElementConstitutifRepository elementConstitutifRepository;
   private final ResultatTestRepository resultatTestRepository;

   public ProgressionService(ElementConstitutifRepository elementConstitutifRepository,
                             ResultatTestRepository resultatTestRepository) {
       this.elementConstitutifRepository = elementConstitutifRepository;
       this.resultatTestRepository = resultatTestRepository;
   }

   /**
    * Récupère la liste des matières d'un étudiant avec le statut de connaissance basé sur les tests.
    */
   @Transactional(readOnly = true)
   public List<MatiereStatutDto> findMatieresByEtudiant(Long etudiantId) {
       List<ElementConstitutif> matieres = elementConstitutifRepository.findMatieresByEtudiantIdSqlNatif(etudiantId);

       return matieres.stream().map(matiere -> {
           List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdAndMatiereId(etudiantId, matiere.getId());
           double totalScore = resultats.stream().mapToDouble(ResultatTest::getScore).sum();
           int nbTests = resultats.size();
           double moyenne = nbTests > 0 ? totalScore / nbTests : 0.0;

           String statut;
           if (moyenne <= 33) statut = "Débutant";
           else if (moyenne <= 66) statut = "Intermédiaire";
           else statut = "Maîtrise";

           return new MatiereStatutDto(
               matiere.getId(),
               matiere.getNom(),
               moyenne,
               matiere.getCode(),
               matiere.getCredit(),
               statut
           );
       }).collect(Collectors.toList());
   }

   /**
    * Récupère la progression détaillée par chapitre pour un étudiant
    * @param etudiantId ID de l'étudiant
    * @param parcoursType Type de parcours à filtrer (optionnel)
    * @return Liste des chapitres avec progression
    */
   @Transactional(readOnly = true)
   public List<ChapitreProgressDto> findChapitresProgressByStudent(Long etudiantId, String parcoursType) {
       // Récupérer tous les résultats de l'étudiant avec les détails
       List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdWithDetails(etudiantId);
       
       // Grouper les résultats par chapitre
       Map<Chapitre, List<ResultatTest>> resultatsParChapitre = new HashMap<>();
       
       for (ResultatTest resultat : resultats) {
           if (resultat.getTest() != null && resultat.getTest().getChapitre() != null) {
               Chapitre chapitre = resultat.getTest().getChapitre();
               // Si un parcoursType est spécifié, on filtre
               if (parcoursType != null && !parcoursType.isEmpty()) {
                   // Vérifier si le chapitre a le même type de parcours
                   if (chapitre.getParcoursType() != null && chapitre.getParcoursType().equals(parcoursType)) {
                       resultatsParChapitre
                           .computeIfAbsent(chapitre, k -> new ArrayList<>())
                           .add(resultat);
                   }
               } else {
                   resultatsParChapitre
                       .computeIfAbsent(chapitre, k -> new ArrayList<>())
                       .add(resultat);
               }
           }
       }
       
       // Convertir en DTOs
       List<ChapitreProgressDto> chapitresProgress = new ArrayList<>();
       
       for (Map.Entry<Chapitre, List<ResultatTest>> entry : resultatsParChapitre.entrySet()) {
           Chapitre chapitre = entry.getKey();
           List<ResultatTest> resultatsChapitre = entry.getValue();
           
           // Calculer la moyenne des scores
           double moyenne = resultatsChapitre.stream()
               .mapToDouble(rt -> {
                   // Utiliser le pourcentage si disponible, sinon calculer
                   if (rt.getScoreTotal() != null && rt.getScoreTotal() > 0) {
                       return (rt.getScore() / rt.getScoreTotal()) * 100;
                   }
                   return rt.getScore();
               })
               .average()
               .orElse(0.0);
           
           // Trouver la date du dernier test
           LocalDateTime dateDernierTest = resultatsChapitre.stream()
               .map(ResultatTest::getDateTest)
               .max(LocalDateTime::compareTo)
               .orElse(null);
           
           // Déterminer le type de parcours (prendre celui du chapitre, sinon le paramètre, sinon "RECOMMANDE")
           String typeParcours = chapitre.getParcoursType() != null ? 
                                 chapitre.getParcoursType() : 
                                 (parcoursType != null ? parcoursType : "RECOMMANDE");
           
           // Créer le DTO
           ChapitreProgressDto dto = new ChapitreProgressDto(
               chapitre.getId(),
               chapitre.getNom(),
               chapitre.getOrdre(),
               moyenne,
               typeParcours,
               dateDernierTest,
               resultatsChapitre.size()
           );
           
           chapitresProgress.add(dto);
       }
       
       // Trier par ordre du chapitre
       chapitresProgress.sort(Comparator.comparing(ChapitreProgressDto::getOrdre));
       
       return chapitresProgress;
   }
   
   /**
    * Récupère la progression agrégée par matière pour un étudiant
    */
   @Transactional(readOnly = true)
   public Map<String, List<ChapitreProgressDto>> findChapitresGroupedByMatiere(Long etudiantId, String parcoursType) {
       List<ChapitreProgressDto> chapitres = findChapitresProgressByStudent(etudiantId, parcoursType);
       
       // Pour grouper par matière, nous avons besoin de l'élément constitutif
       // Cette implémentation nécessite d'avoir accès à l'élément constitutif depuis le chapitre
       Map<String, List<ChapitreProgressDto>> groupedByMatiere = new HashMap<>();
       
       // Note: Cette implémentation nécessite que Chapitre ait une relation avec ElementConstitutif
       // À adapter selon votre modèle de données
       for (ChapitreProgressDto chapitre : chapitres) {
           // Ici, nous devrions récupérer le nom de la matière
           // Pour l'instant, nous utilisons un placeholder
           String matiereNom = "Matière inconnue";
           groupedByMatiere
               .computeIfAbsent(matiereNom, k -> new ArrayList<>())
               .add(chapitre);
       }
       
       return groupedByMatiere;
   }
}