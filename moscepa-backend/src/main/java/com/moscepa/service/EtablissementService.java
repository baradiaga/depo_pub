package com.moscepa.service;

import com.moscepa.dto.EtablissementDTO;
import com.moscepa.entity.Etablissement;
import com.moscepa.exception.ResourceNotFoundException;
import com.moscepa.repository.EtablissementRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EtablissementService {
    
    private final EtablissementRepository etablissementRepository;
    private final ModelMapper modelMapper;
    
    @Autowired
    public EtablissementService(EtablissementRepository etablissementRepository, ModelMapper modelMapper) {
        this.etablissementRepository = etablissementRepository;
        this.modelMapper = modelMapper;
    }
    
    public List<EtablissementDTO> getAllEtablissements() {
        return etablissementRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public EtablissementDTO getEtablissementById(Long id) {
        Etablissement etablissement = etablissementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé avec l'ID : " + id));
        return convertToDTO(etablissement);
    }
    
    public EtablissementDTO createEtablissement(EtablissementDTO etablissementDTO) {
        // Vérifier l'unicité du sigle
        if (etablissementRepository.existsBySigle(etablissementDTO.getSigle())) {
            throw new IllegalArgumentException("Un établissement avec ce sigle existe déjà");
        }
        
        Etablissement etablissement = convertToEntity(etablissementDTO);
        Etablissement savedEtablissement = etablissementRepository.save(etablissement);
        return convertToDTO(savedEtablissement);
    }
    
    public EtablissementDTO updateEtablissement(Long id, EtablissementDTO etablissementDTO) {
        Etablissement existingEtablissement = etablissementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Établissement non trouvé avec l'ID : " + id));
        
        // Vérifier l'unicité du sigle si modifié
        if (!existingEtablissement.getSigle().equals(etablissementDTO.getSigle()) &&
            etablissementRepository.existsBySigleAndIdNot(etablissementDTO.getSigle(), id)) {
            throw new IllegalArgumentException("Un établissement avec ce sigle existe déjà");
        }
        
        // Mettre à jour les champs
        existingEtablissement.setNom(etablissementDTO.getNom());
        existingEtablissement.setSigle(etablissementDTO.getSigle());
        existingEtablissement.setAdresse(etablissementDTO.getAdresse());
        existingEtablissement.setContact(etablissementDTO.getContact());
        existingEtablissement.setLogo(etablissementDTO.getLogo());
        existingEtablissement.setLien(etablissementDTO.getLien());
        
        Etablissement updatedEtablissement = etablissementRepository.save(existingEtablissement);
        return convertToDTO(updatedEtablissement);
    }
    
    public void deleteEtablissement(Long id) {
        if (!etablissementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Établissement non trouvé avec l'ID : " + id);
        }
        etablissementRepository.deleteById(id);
    }
    
    private EtablissementDTO convertToDTO(Etablissement etablissement) {
        return modelMapper.map(etablissement, EtablissementDTO.class);
    }
    
    private Etablissement convertToEntity(EtablissementDTO etablissementDTO) {
        return modelMapper.map(etablissementDTO, Etablissement.class);
    }
}