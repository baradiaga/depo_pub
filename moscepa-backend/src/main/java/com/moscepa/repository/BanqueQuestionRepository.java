// Fichier : src/main/java/com/moscepa/repository/BanqueQuestionRepository.java

package com.moscepa.repository;
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.BanqueQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanqueQuestionRepository extends JpaRepository<BanqueQuestion, Long>, JpaSpecificationExecutor<BanqueQuestion> {

    // Méthode pour trouver les questions par chapitre
    List<BanqueQuestion> findByChapitreId(Long chapitreId);

    // Méthode pour trouver les questions par auteur
    List<BanqueQuestion> findByAuteurId(Long auteurId);
        // ✅ Trouver toutes les questions d’un chapitre
    List<BanqueQuestion> findByChapitre(Chapitre chapitre);

    // ✅ Trouver toutes les questions dont le chapitre est dans une liste d’IDs
    List<BanqueQuestion> findByChapitreIdIn(List<Long> chapitresIds);
}
