package com.moscepa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
// import org.modelmapper.ModelMapper; // <-- SUPPRIMEZ OU COMMENZ CET IMPORT
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moscepa.dto.ResponsableDto;
import com.moscepa.dto.UniteEnseignementDto;
import com.moscepa.entity.UniteEnseignement;
import com.moscepa.entity.Utilisateur;
import com.moscepa.repository.UniteEnseignementRepository;
import com.moscepa.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class UniteEnseignementService {

    @Autowired
    private UniteEnseignementRepository ueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // @Autowired
    // private ModelMapper modelMapper; // <-- SUPPRIMEZ OU COMMENZ CETTE INJECTION

    @Transactional(readOnly = true)
    public List<UniteEnseignementDto> findAll() {
        return ueRepository.findAll().stream().map(this::convertToDto) // Appelle notre méthode
                                                                       // manuelle
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<UniteEnseignementDto> findById(Long id) {
        return ueRepository.findById(id).map(this::convertToDto); // Appelle notre méthode manuelle
    }

    @Transactional
    public UniteEnseignementDto save(UniteEnseignementDto ueDto) {
        UniteEnseignement ue = convertToEntity(ueDto); // Appelle notre méthode manuelle
        UniteEnseignement savedUe = ueRepository.save(ue);
        return convertToDto(savedUe);
    }

    @Transactional
    public UniteEnseignementDto update(Long id, UniteEnseignementDto ueDto) {
        UniteEnseignement existingUe =
                ueRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                        "Unité d'enseignement non trouvée avec l'id: " + id));

        // Mise à jour manuelle des champs en utilisant la syntaxe des records
        existingUe.setNom(ueDto.nom());
        existingUe.setCode(ueDto.code());
        existingUe.setDescription(ueDto.description());
        existingUe.setCredit(ueDto.credit());
        existingUe.setSemestre(ueDto.semestre());
        existingUe.setObjectifs(ueDto.objectifs());

        if (ueDto.responsable() != null && ueDto.responsable().id() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.responsable().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Responsable non trouvé avec l'id: " + ueDto.responsable().id()));
            existingUe.setResponsable(responsable);
        } else {
            existingUe.setResponsable(null);
        }

        UniteEnseignement updatedUe = ueRepository.save(existingUe);
        return convertToDto(updatedUe);
    }

    public void deleteById(Long id) {
        if (!ueRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Impossible de supprimer, Unité d'enseignement non trouvée avec l'id: " + id);
        }
        ueRepository.deleteById(id);
    }

    // --- MÉTHODES DE CONVERSION MANUELLE (COMPATIBLES AVEC LES RECORD) ---

    private UniteEnseignementDto convertToDto(UniteEnseignement ue) {
        ResponsableDto responsableDto = null;
        if (ue.getResponsable() != null) {
            // On utilise le constructeur du record ResponsableDto
            responsableDto = new ResponsableDto(ue.getResponsable().getId(),
                    ue.getResponsable().getNom(), ue.getResponsable().getPrenom());
        }

        // On utilise le constructeur du record UniteEnseignementDto
        return new UniteEnseignementDto(ue.getId(), ue.getNom(), ue.getCode(), ue.getDescription(),
                ue.getCredit(), ue.getSemestre(), ue.getObjectifs(), responsableDto);
    }

    private UniteEnseignement convertToEntity(UniteEnseignementDto ueDto) {
        UniteEnseignement ue = new UniteEnseignement();

        // On utilise la syntaxe des records (dto.nom()) pour lire les valeurs
        ue.setId(ueDto.id());
        ue.setNom(ueDto.nom());
        ue.setCode(ueDto.code());
        ue.setDescription(ueDto.description());
        ue.setCredit(ueDto.credit());
        ue.setSemestre(ueDto.semestre());
        ue.setObjectifs(ueDto.objectifs());

        if (ueDto.responsable() != null && ueDto.responsable().id() != null) {
            Utilisateur responsable = utilisateurRepository.findById(ueDto.responsable().id())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Impossible de créer/mettre à jour l'UE car le responsable avec l'id: "
                                    + ueDto.responsable().id() + " n'a pas été trouvé."));
            ue.setResponsable(responsable);
        }

        return ue;
    }
}
