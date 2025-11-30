package com.moscepa.repository;

import com.moscepa.entity.BanqueReponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanqueReponseRepository extends JpaRepository<BanqueReponse, Long> {
    // tu peux ajouter des méthodes de recherche personnalisées ici si besoin
}
