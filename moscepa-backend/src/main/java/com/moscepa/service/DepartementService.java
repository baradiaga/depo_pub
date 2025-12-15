package com.moscepa.service;

import com.moscepa.dto.DepartementDTO;
import com.moscepa.dto.UefrDTO;
import com.moscepa.entity.Departement;
import com.moscepa.entity.Uefr;
import com.moscepa.exception.ResourceNotFoundException;
import com.moscepa.repository.DepartementRepository;
import com.moscepa.repository.UefrRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartementService {
    
    private final DepartementRepository departementRepository;
    private final UefrRepository uefrRepository;
    private final ModelMapper modelMapper;
    
    @Autowired
    public DepartementService(DepartementRepository departementRepository, 
                             UefrRepository uefrRepository, 
                             ModelMapper modelMapper) {
        this.departementRepository = departementRepository;
        this.uefrRepository = uefrRepository;
        this.modelMapper = modelMapper;
    }
    
    public List<DepartementDTO> getAllDepartements() {
        return departementRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<DepartementDTO> getDepartementsByUefrId(Long uefrId) {
        return departementRepository.findByUefrId(uefrId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public DepartementDTO getDepartementById(Long id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé avec l'ID : " + id));
        return convertToDTO(departement);
    }
    
    public DepartementDTO createDepartement(DepartementDTO departementDTO) {
        // Vérifier si l'UEFR est fournie
        if (departementDTO.getUefr() == null || departementDTO.getUefr().getId() == null) {
            throw new IllegalArgumentException("L'UEFR est obligatoire");
        }
        
        // Récupérer l'UEFR
        Uefr uefr = uefrRepository.findById(departementDTO.getUefr().getId())
                .orElseThrow(() -> new ResourceNotFoundException("UEFR non trouvée avec l'ID : " + departementDTO.getUefr().getId()));
        
        // Vérifier l'unicité du sigle dans l'UEFR
        if (departementRepository.existsBySigleAndUefr(departementDTO.getSigle(), uefr)) {
            throw new IllegalArgumentException("Un département avec ce sigle existe déjà dans cette UEFR");
        }
        
        // Convertir et sauvegarder
        Departement departement = convertToEntity(departementDTO);
        departement.setUefr(uefr);
        
        Departement savedDepartement = departementRepository.save(departement);
        return convertToDTO(savedDepartement);
    }
    
    public DepartementDTO updateDepartement(Long id, DepartementDTO departementDTO) {
        // Vérifier l'existence du département
        Departement existingDepartement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département non trouvé avec l'ID : " + id));
        
        // Vérifier si l'UEFR est fournie
        if (departementDTO.getUefr() == null || departementDTO.getUefr().getId() == null) {
            throw new IllegalArgumentException("L'UEFR est obligatoire");
        }
        
        // Récupérer l'UEFR
        Uefr uefr = uefrRepository.findById(departementDTO.getUefr().getId())
                .orElseThrow(() -> new ResourceNotFoundException("UEFR non trouvée avec l'ID : " + departementDTO.getUefr().getId()));
        
        // Vérifier l'unicité du sigle si changé
        if (!existingDepartement.getSigle().equals(departementDTO.getSigle()) &&
            departementRepository.existsBySigleAndUefr(departementDTO.getSigle(), uefr)) {
            throw new IllegalArgumentException("Un département avec ce sigle existe déjà dans cette UEFR");
        }
        
        // Mettre à jour les champs
        existingDepartement.setNom(departementDTO.getNom());
        existingDepartement.setSigle(departementDTO.getSigle());
        existingDepartement.setAdresse(departementDTO.getAdresse());
        existingDepartement.setContact(departementDTO.getContact());
        existingDepartement.setLogo(departementDTO.getLogo());
        existingDepartement.setLien(departementDTO.getLien());
        existingDepartement.setUefr(uefr);
        
        Departement updatedDepartement = departementRepository.save(existingDepartement);
        return convertToDTO(updatedDepartement);
    }
    
    public void deleteDepartement(Long id) {
        if (!departementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Département non trouvé avec l'ID : " + id);
        }
        departementRepository.deleteById(id);
    }
    
    // Méthodes de conversion
    private DepartementDTO convertToDTO(Departement departement) {
        DepartementDTO dto = modelMapper.map(departement, DepartementDTO.class);
        
        // Convertir l'UEFR en DTO
        if (departement.getUefr() != null) {
            UefrDTO uefrDTO = modelMapper.map(departement.getUefr(), UefrDTO.class);
            dto.setUefr(uefrDTO);
        }
        
        return dto;
    }
    
    private Departement convertToEntity(DepartementDTO departementDTO) {
        Departement departement = modelMapper.map(departementDTO, Departement.class);
        return departement;
    }
}