package com.moscepa.service;

import com.moscepa.dto.ChapitreDto; // <-- Assurez-vous que cet import est présent
import com.moscepa.dto.MatiereDto;
import com.moscepa.entity.Matiere;
import com.moscepa.repository.MatiereRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;

    // Supposons que vous n'utilisez pas ModelMapper pour l'instant,
    // car la méthode convertToDto est manuelle.
    // @Autowired
    // private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<MatiereDto> getMatieres() {
        return matiereRepository.findAllOrderByNom()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<MatiereDto> getMatiereById(Long id) {
        return matiereRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<String> getMatieresDisponibles() {
        return matiereRepository.findAllNoms();
    }

    public MatiereDto createMatiere(MatiereDto matiereDto) {
        if (matiereRepository.existsByNom(matiereDto.getNom())) {
            throw new RuntimeException("Une matière avec ce nom existe déjà");
        }
        Matiere matiere = new Matiere();
        matiere.setNom(matiereDto.getNom());
        matiere.setDescription(matiereDto.getDescription());
        matiere = matiereRepository.save(matiere);
        return convertToDto(matiere);
    }

    public MatiereDto updateMatiere(Long id, MatiereDto matiereDto) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'ID: " + id));
        if (!matiere.getNom().equals(matiereDto.getNom())) {
            if (matiereRepository.existsByNom(matiereDto.getNom())) {
                throw new RuntimeException("Une matière avec ce nom existe déjà");
            }
        }
        matiere.setNom(matiereDto.getNom());
        matiere.setDescription(matiereDto.getDescription());
        matiere = matiereRepository.save(matiere);
        return convertToDto(matiere);
    }

    public void deleteMatiere(Long id) {
        if (!matiereRepository.existsById(id)) {
            throw new RuntimeException("Matière non trouvée avec l'ID: " + id);
        }
        matiereRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MatiereDto> searchMatieres(String nom) {
        return matiereRepository.findByNomContainingIgnoreCase(nom)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MatiereDto convertToDto(Matiere matiere) {
        MatiereDto dto = new MatiereDto();
        dto.setId(matiere.getId());
        dto.setNom(matiere.getNom());
        dto.setDescription(matiere.getDescription());
        return dto;
    }

    // ====================================================================
    // AJOUT : Les deux méthodes nécessaires pour le composant 'ListeMatieresComponent'
    // ====================================================================

    /**
     * Récupère toutes les matières et les convertit en DTOs,
     * en incluant pour chacune la liste détaillée de ses chapitres.
     * C'est la méthode que le contrôleur va appeler.
     */
    @Transactional(readOnly = true)
    public List<MatiereDto> getMatieresAvecDetails() {
        List<Matiere> matieres = matiereRepository.findAll();
        return matieres.stream()
                .map(this::convertirMatiereEnDtoAvecChapitres)
                .collect(Collectors.toList());
    }

    /**
     * Méthode utilitaire privée pour convertir une entité Matiere en DTO
     * en s'assurant que la liste des chapitres est également convertie et remplie.
     */
    private MatiereDto convertirMatiereEnDtoAvecChapitres(Matiere matiere) {
        // On commence par la conversion de base
        MatiereDto matiereDto = convertToDto(matiere);

        // Puis, on convertit et on ajoute la liste des chapitres
        List<ChapitreDto> chapitreDtos = matiere.getChapitres().stream()
            .map(chapitre -> {
                ChapitreDto chapitreDto = new ChapitreDto();
                chapitreDto.setId(chapitre.getId());
                chapitreDto.setNom(chapitre.getNom());
                chapitreDto.setNiveau(chapitre.getNiveau());
                chapitreDto.setObjectif(chapitre.getObjectif());
                return chapitreDto;
            })
            .collect(Collectors.toList());
        
        matiereDto.setChapitres(chapitreDtos);
        return matiereDto;
    }
}