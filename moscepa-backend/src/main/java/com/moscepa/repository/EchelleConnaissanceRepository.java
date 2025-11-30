// Fichier : src/main/java/com/moscepa/repository/EchelleConnaissanceRepository.java

package com.moscepa.repository;

import com.moscepa.entity.EchelleConnaissance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EchelleConnaissanceRepository extends JpaRepository<EchelleConnaissance, Long> {
}
