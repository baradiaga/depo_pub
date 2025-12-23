package com.moscepa.service;

import com.moscepa.dto.ChapitreProgressDto;
import com.moscepa.dto.ChapitreProgressFrontDto;
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
   private final FrontendMapperService mapperService;

   public ProgressionService(ElementConstitutifRepository elementConstitutifRepository,
                             ResultatTestRepository resultatTestRepository,
                             FrontendMapperService mapperService) {
       this.elementConstitutifRepository = elementConstitutifRepository;
       this.resultatTestRepository = resultatTestRepository;
       this.mapperService = mapperService;
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
    */
   @Transactional(readOnly = true)
   public List<ChapitreProgressDto> findChapitresProgressByStudent(Long etudiantId, String parcoursType) {
       List<ResultatTest> resultats = resultatTestRepository.findByEtudiantIdWithDetails(etudiantId);
       
       Map<Chapitre, List<ResultatTest>> resultatsParChapitre = new HashMap<>();
       
       for (ResultatTest resultat : resultats) {
           if (resultat.getTest() != null && resultat.getTest().getChapitre() != null) {
               Chapitre chapitre = resultat.getTest().getChapitre();
               if (parcoursType != null && !parcoursType.isEmpty()) {
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
       
       List<ChapitreProgressDto> chapitresProgress = new ArrayList<>();
       
       for (Map.Entry<Chapitre, List<ResultatTest>> entry : resultatsParChapitre.entrySet()) {
           Chapitre chapitre = entry.getKey();
           List<ResultatTest> resultatsChapitre = entry.getValue();
           
           double moyenne = resultatsChapitre.stream()
               .mapToDouble(rt -> {
                   if (rt.getScoreTotal() != null && rt.getScoreTotal() > 0) {
                       return (rt.getScore() / rt.getScoreTotal()) * 100;
                   }
                   return rt.getScore();
               })
               .average()
               .orElse(0.0);
           
           LocalDateTime dateDernierTest = resultatsChapitre.stream()
               .map(ResultatTest::getDateTest)
               .max(LocalDateTime::compareTo)
               .orElse(null);
           
           String typeParcours = chapitre.getParcoursType() != null ? 
                                 chapitre.getParcoursType() : 
                                 (parcoursType != null ? parcoursType : "RECOMMANDE");
           
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
       
       chapitresProgress.sort(Comparator.comparing(ChapitreProgressDto::getOrdre));
       
       return chapitresProgress;
   }
   
   /**
    * Version frontend de findChapitresProgressByStudent
    */
   public List<ChapitreProgressFrontDto> findChapitresProgressForFrontend(
           Long etudiantId, String parcoursType) {
       
       List<ChapitreProgressDto> backendList = 
           findChapitresProgressByStudent(etudiantId, parcoursType);
       
       return mapperService.toChapitreFrontDtoList(backendList);
   }
   
   /**
    * Récupère la progression agrégée par matière pour un étudiant
    */
   @Transactional(readOnly = true)
   public Map<String, List<ChapitreProgressDto>> findChapitresGroupedByMatiere(Long etudiantId, String parcoursType) {
       List<ChapitreProgressDto> chapitres = findChapitresProgressByStudent(etudiantId, parcoursType);
       
       Map<String, List<ChapitreProgressDto>> groupedByMatiere = new HashMap<>();
       
       for (ChapitreProgressDto chapitre : chapitres) {
           // TODO: Implémenter la récupération du vrai nom de la matière
           // Pour l'instant, nous utilisons un placeholder
           String matiereNom = "Matière inconnue";
           groupedByMatiere
               .computeIfAbsent(matiereNom, k -> new ArrayList<>())
               .add(chapitre);
       }
       
       return groupedByMatiere;
   }
   
   /**
    * Version frontend de findChapitresGroupedByMatiere
    */
   public Map<String, List<ChapitreProgressFrontDto>> findChapitresGroupedForFrontend(
           Long etudiantId, String parcoursType) {
       
       Map<String, List<ChapitreProgressDto>> backendMap = 
           findChapitresGroupedByMatiere(etudiantId, parcoursType);
       
       Map<String, List<ChapitreProgressFrontDto>> frontMap = new HashMap<>();
       
       backendMap.forEach((matiere, chapitres) -> {
           List<ChapitreProgressFrontDto> frontChapitres = 
               mapperService.toChapitreFrontDtoList(chapitres);
           frontMap.put(matiere, frontChapitres);
       });
       
       return frontMap;
   }
}