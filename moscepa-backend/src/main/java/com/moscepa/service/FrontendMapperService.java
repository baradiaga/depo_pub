// FrontendMapperService.java
package com.moscepa.service;

import com.moscepa.dto.StudentJourneyDto;
import com.moscepa.dto.CourseProgressDto;
import com.moscepa.dto.ChapitreProgressDto;
import com.moscepa.dto.StudentJourneyFrontDto;
import com.moscepa.dto.ChapitreProgressFrontDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FrontendMapperService {
    
    /**
     * Convertit StudentJourneyDto (backend) en StudentJourneyFrontDto (frontend)
     */
    public StudentJourneyFrontDto toFrontDto(StudentJourneyDto backendDto) {
        if (backendDto == null) {
            return null;
        }
        
        StudentJourneyFrontDto frontDto = new StudentJourneyFrontDto();
        
        // Mapper les propriétés directement (mêmes noms dans les deux DTOs)
        frontDto.setStudentId(backendDto.getStudentId());
        frontDto.setNomComplet(backendDto.getNomComplet());
        frontDto.setEmail(backendDto.getEmail());
        frontDto.setFormationActuelle(backendDto.getFormationActuelle());
        frontDto.setNiveauEtude(backendDto.getNiveauEtude());
        frontDto.setMoyenneGeneraleTests(backendDto.getMoyenneGeneraleTests());
        frontDto.setTestsPasses(backendDto.getTestsPasses());
        frontDto.setParcoursType(backendDto.getParcoursType());
        
        // La progressionParCours est déjà du type CourseProgressDto qui convient au frontend
        frontDto.setProgressionParCours(backendDto.getProgressionParCours());
        
        return frontDto;
    }
    
    /**
     * Convertit ChapitreProgressDto (backend) en ChapitreProgressFrontDto (frontend)
     */
    public ChapitreProgressFrontDto toFrontDto(ChapitreProgressDto backendDto) {
        if (backendDto == null) {
            return null;
        }
        
        return new ChapitreProgressFrontDto(
            backendDto.getChapitreId(),
            backendDto.getChapitreNom(),
            backendDto.getOrdre(),
            backendDto.getScoreMoyen(),
            backendDto.getParcoursType(),
            backendDto.getDateDernierTest(),
            backendDto.getNombreTests()
        );
    }
    
    /**
     * Convertit une liste de StudentJourneyDto
     */
    public List<StudentJourneyFrontDto> toFrontDtoList(List<StudentJourneyDto> backendList) {
        if (backendList == null) {
            return List.of();
        }
        
        return backendList.stream()
            .map(this::toFrontDto)
            .collect(Collectors.toList());
    }
    
    /**
     * Convertit une liste de ChapitreProgressDto
     */
    public List<ChapitreProgressFrontDto> toChapitreFrontDtoList(List<ChapitreProgressDto> backendList) {
        if (backendList == null) {
            return List.of();
        }
        
        return backendList.stream()
            .map(this::toFrontDto)
            .collect(Collectors.toList());
    }
}