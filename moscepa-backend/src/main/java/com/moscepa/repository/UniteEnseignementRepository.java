package com.moscepa.repository;

import com.moscepa.entity.UniteEnseignement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- Assurez-vous que cet import est présent

@Repository
public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, Long> {

    // ======================== AJOUTEZ CECI ========================
    /**
     * Trouve une Unité d'Enseignement par son code.
     * Le code est supposé être un identifiant unique.
     * @param code Le code de l'UE à rechercher.
     * @return un Optional contenant l'UE si elle est trouvée, sinon un Optional vide.
     */
    Optional<UniteEnseignement> findByCode(String code);
    
    // ==============================================================
}
