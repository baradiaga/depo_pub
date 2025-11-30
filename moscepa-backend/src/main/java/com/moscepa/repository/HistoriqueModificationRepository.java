// Fichier : src/main/java/com/moscepa/repository/HistoriqueModificationRepository.java

package com.moscepa.repository;

import com.moscepa.entity.HistoriqueModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueModificationRepository extends JpaRepository<HistoriqueModification, Long> {

    // Méthode pour trouver l'historique d'une question spécifique
    List<HistoriqueModification> findByBanqueQuestionIdOrderByDateModificationDesc(Long banqueQuestionId);
}
