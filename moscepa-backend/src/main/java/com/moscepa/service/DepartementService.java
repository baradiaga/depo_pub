package com.moscepa.service;

import com.moscepa.dto.DepartementDTO;
import com.moscepa.entity.Departement;
import com.moscepa.entity.Formation;
import com.moscepa.entity.Uefr;
import com.moscepa.repository.DepartementRepository;
import com.moscepa.repository.FormationRepository;
import com.moscepa.repository.UefrRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartementService {
    
    @Autowired
    private DepartementRepository departementRepository;
    
    @Autowired
    private UefrRepository uefrRepository;
    
    @Autowired
    private FormationRepository formationRepository;
    
    // ========== MÉTHODES CRUD ==========
    
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }
    
    public Departement getDepartementById(Long id) {
        return departementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + id));
    }
    
    public Departement createDepartement(DepartementDTO departementDTO) {
        // Vérifier si le sigle existe déjà
        if (departementRepository.existsBySigle(departementDTO.getSigle())) {
            throw new RuntimeException("Le sigle '" + departementDTO.getSigle() + "' existe déjà");
        }
        
        Departement departement = new Departement();
        
        // Mapper les champs de base
        departement.setNom(departementDTO.getNom());
        departement.setSigle(departementDTO.getSigle());
        departement.setAdresse(departementDTO.getAdresse());
        departement.setContact(departementDTO.getContact());
        departement.setLogo(departementDTO.getLogo());
        departement.setLien(departementDTO.getLien());
        
        // Gérer l'UEFR
        Uefr uefr = uefrRepository.findById(departementDTO.getUefrId())
            .orElseThrow(() -> new RuntimeException("UEFR non trouvée avec l'ID: " + departementDTO.getUefrId()));
        departement.setUefr(uefr);
        
        // Gérer la Formation (si spécifiée)
        if (departementDTO.getFormationId() != null) {
            Formation formation = formationRepository.findById(departementDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + departementDTO.getFormationId()));
            departement.setFormation(formation);
        }
        
        return departementRepository.save(departement);
    }
    
    public Departement updateDepartement(Long id, DepartementDTO departementDTO) {
        Departement departement = departementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'ID: " + id));
        
        // Vérifier si le sigle existe pour un autre département
        if (!departement.getSigle().equals(departementDTO.getSigle()) 
            && departementRepository.existsBySigle(departementDTO.getSigle())) {
            throw new RuntimeException("Le sigle '" + departementDTO.getSigle() + "' est déjà utilisé");
        }
        
        // Mettre à jour les champs
        departement.setNom(departementDTO.getNom());
        departement.setSigle(departementDTO.getSigle());
        departement.setAdresse(departementDTO.getAdresse());
        departement.setContact(departementDTO.getContact());
        departement.setLogo(departementDTO.getLogo());
        departement.setLien(departementDTO.getLien());
        
        // Mettre à jour l'UEFR
        if (!departement.getUefr().getId().equals(departementDTO.getUefrId())) {
            Uefr uefr = uefrRepository.findById(departementDTO.getUefrId())
                .orElseThrow(() -> new RuntimeException("UEFR non trouvée avec l'ID: " + departementDTO.getUefrId()));
            departement.setUefr(uefr);
        }
        
        // Mettre à jour la Formation
        Long formationId = departementDTO.getFormationId();
        if (formationId == null) {
            departement.setFormation(null);
        } else if (departement.getFormation() == null || !departement.getFormation().getId().equals(formationId)) {
            Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID: " + formationId));
            departement.setFormation(formation);
        }
        
        return departementRepository.save(departement);
    }
    
    public void deleteDepartement(Long id) {
        if (!departementRepository.existsById(id)) {
            throw new RuntimeException("Département non trouvé avec l'ID: " + id);
        }
        departementRepository.deleteById(id);
    }
    
    // ========== RECHERCHE AVEC PAGINATION ==========
    
    public Page<Departement> search(String searchTerm, Long uefrId, Pageable pageable) {
        if ((searchTerm == null || searchTerm.trim().isEmpty()) && uefrId == null) {
            return departementRepository.findAll(pageable);
        }
        
        return departementRepository.search(
            (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null,
            uefrId,
            pageable
        );
    }
    
    // Méthode search avec formationId (si besoin)
    public Page<Departement> search(String searchTerm, Long uefrId, Long formationId, Pageable pageable) {
        if ((searchTerm == null || searchTerm.trim().isEmpty()) && uefrId == null && formationId == null) {
            return departementRepository.findAll(pageable);
        }
        
        return departementRepository.search(
            (searchTerm != null && !searchTerm.trim().isEmpty()) ? searchTerm.trim() : null,
            uefrId,
            formationId,
            pageable
        );
    }
    
    // ========== MÉTHODES UTILITAIRES ==========
    
    public boolean existsBySigle(String sigle) {
        return departementRepository.existsBySigle(sigle);
    }
    
    public boolean existsBySigleAndIdNot(String sigle, Long id) {
        return departementRepository.existsBySigleAndIdNot(sigle, id);
    }
}