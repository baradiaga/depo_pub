// Fichier : src/main/java/com/moscepa/repository/RessourcePedagogiqueRepository.java

package com.moscepa.repository;

import com.moscepa.entity.RessourcePedagogique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourcePedagogiqueRepository extends JpaRepository<RessourcePedagogique, Long>, JpaSpecificationExecutor<RessourcePedagogique> {

    List<RessourcePedagogique> findByChapitreId(Long chapitreId);
}
