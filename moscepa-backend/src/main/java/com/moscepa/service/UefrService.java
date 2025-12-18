package com.moscepa.service;

import com.moscepa.dto.UefrDTO;
import com.moscepa.entity.Etablissement;
import com.moscepa.entity.Uefr;
import com.moscepa.exception.ResourceNotFoundException;
import com.moscepa.repository.EtablissementRepository;
import com.moscepa.repository.UefrRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UefrService {
    
    private final UefrRepository uefrRepository;
    private final EtablissementRepository etablissementRepository;
    private final ModelMapper modelMapper;
    
    @Autowired
    public UefrService(UefrRepository uefrRepository, 
                      EtablissementRepository etablissementRepository, 
                      ModelMapper modelMapper) {
        this.uefrRepository = uefrRepository;
        this.etablissementRepository = etablissementRepository;
        this.modelMapper = modelMapper;
    }
    
    // ========== CRUD DE BASE (IDs seulement) ==========
    
    public List<UefrDTO> getAllUefrs() {
        return uefrRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public UefrDTO getUefrById(Long id) {
        Uefr uefr = uefrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UEFR non trouvé avec l'ID : " + id));
        return convertToDTO(uefr);
    }
    
    public UefrDTO createUefr(UefrDTO uefrDTO) {
        // Vérifier si l'établissement existe
        Etablissement etablissement = etablissementRepository.findById(uefrDTO.getEtablissementId())
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé avec l'ID : " + uefrDTO.getEtablissementId()));
        
        // Vérifier l'unicité du sigle
        if (uefrRepository.existsBySigle(uefrDTO.getSigle())) {
            throw new IllegalArgumentException("Une UEFR avec ce sigle existe déjà");
        }
        
        // Convertir et sauvegarder
        Uefr uefr = convertToEntity(uefrDTO);
        uefr.setEtablissement(etablissement);
        
        Uefr savedUefr = uefrRepository.save(uefr);
        return convertToDTO(savedUefr);
    }
    
    public UefrDTO updateUefr(Long id, UefrDTO uefrDTO) {
        // Vérifier l'existence de l'UEFR
        Uefr existingUefr = uefrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UEFR non trouvé avec l'ID : " + id));
        
        // Vérifier si l'établissement existe
        Etablissement etablissement = etablissementRepository.findById(uefrDTO.getEtablissementId())
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé avec l'ID : " + uefrDTO.getEtablissementId()));
        
        // Vérifier l'unicité du sigle si changé
        if (!existingUefr.getSigle().equals(uefrDTO.getSigle()) &&
            uefrRepository.existsBySigleAndIdNot(uefrDTO.getSigle(), id)) {
            throw new IllegalArgumentException("Une UEFR avec ce sigle existe déjà");
        }
        
        // Mettre à jour les champs
        existingUefr.setNom(uefrDTO.getNom());
        existingUefr.setSigle(uefrDTO.getSigle());
        existingUefr.setAdresse(uefrDTO.getAdresse());
        existingUefr.setContact(uefrDTO.getContact());
        existingUefr.setLogo(uefrDTO.getLogo());
        existingUefr.setLien(uefrDTO.getLien());
        existingUefr.setEtablissement(etablissement);
        
        Uefr updatedUefr = uefrRepository.save(existingUefr);
        return convertToDTO(updatedUefr);
    }
    
    public void deleteUefr(Long id) {
        if (!uefrRepository.existsById(id)) {
            throw new ResourceNotFoundException("UEFR non trouvé avec l'ID : " + id);
        }
        uefrRepository.deleteById(id);
    }
    
    public List<UefrDTO> getUefrsByEtablissementId(Long etablissementId) {
        return uefrRepository.findByEtablissementId(etablissementId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // ========== MÉTHODES DE CONVERSION ==========
    
    private UefrDTO convertToDTO(Uefr uefr) {
        UefrDTO dto = new UefrDTO();
        dto.setId(uefr.getId());
        dto.setNom(uefr.getNom());
        dto.setSigle(uefr.getSigle());
        dto.setAdresse(uefr.getAdresse());
        dto.setContact(uefr.getContact());
        dto.setLogo(uefr.getLogo());
        dto.setLien(uefr.getLien());
        dto.setEtablissementId(uefr.getEtablissement().getId()); // ← ID seulement
        dto.setCreatedAt(uefr.getCreatedAt());
        dto.setUpdatedAt(uefr.getUpdatedAt());
        return dto;
    }
    
    private Uefr convertToEntity(UefrDTO uefrDTO) {
        Uefr uefr = new Uefr();
        uefr.setNom(uefrDTO.getNom());
        uefr.setSigle(uefrDTO.getSigle());
        uefr.setAdresse(uefrDTO.getAdresse());
        uefr.setContact(uefrDTO.getContact());
        uefr.setLogo(uefrDTO.getLogo());
        uefr.setLien(uefrDTO.getLien());
        return uefr;
    }
}