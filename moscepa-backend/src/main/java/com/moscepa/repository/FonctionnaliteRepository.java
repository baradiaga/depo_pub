package com.moscepa.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.moscepa.entity.Fonctionnalite;

@Repository
public interface FonctionnaliteRepository extends JpaRepository<Fonctionnalite, Long> {

    /**
     * Utilise une LEFT JOIN FETCH pour récupérer toutes les fonctionnalités, y compris celles qui
     * n'ont pas de sous-fonctionnalités. C'est plus robuste que la INNER JOIN par défaut.
     */
    @Query("SELECT DISTINCT f FROM Fonctionnalite f LEFT JOIN FETCH f.sousFonctionnalites")
    @Override
    List<Fonctionnalite> findAll();
}
