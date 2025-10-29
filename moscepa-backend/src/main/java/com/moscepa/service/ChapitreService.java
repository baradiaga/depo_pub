package com.moscepa.service;

import com.moscepa.dto.ChapitreCreateDto;
import com.moscepa.dto.ChapitreDetailDto; // <-- Import du DTO de réponse
import com.moscepa.entity.Chapitre;
import com.moscepa.entity.Matiere;
import com.moscepa.entity.Section;
import com.moscepa.repository.ChapitreRepository;
import com.moscepa.repository.MatiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service contenant la logique métier pour la gestion des chapitres.
 */
@Service
public class ChapitreService {

    private final ChapitreRepository chapitreRepository;
    private final MatiereRepository matiereRepository;
    
    // DTO simple pour la liste, si ChapitreDetailDto est trop lourd
    // Je vais utiliser ChapitreDetailDto pour l'instant, mais je pourrais créer un ChapitreListDto plus tard.
    public record ChapitreListDto(Long id, String titre, String matiereNom) {}

    @Autowired
    public ChapitreService(ChapitreRepository chapitreRepository, MatiereRepository matiereRepository) {
        this.chapitreRepository = chapitreRepository;
        this.matiereRepository = matiereRepository;
    }

    /**
     * Récupère tous les chapitres sous forme de DTO de détail.
     */
    @Transactional(readOnly = true)
    public List<ChapitreDetailDto> findAll() {
        return chapitreRepository.findAll().stream()
                .map(ChapitreDetailDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un chapitre par son ID.
     */
    @Transactional(readOnly = true)
    public Optional<ChapitreDetailDto> findById(Long id) {
        return chapitreRepository.findById(id)
                .map(ChapitreDetailDto::new);
    }

    /**
     * Met à jour un chapitre existant.
     * NOTE: L'implémentation complète des sections est complexe (diffing).
     * Pour l'instant, on se concentre sur les champs principaux.
     */
    @Transactional
    public ChapitreDetailDto updateChapitre(Long id, ChapitreCreateDto dto) {
        Chapitre existingChapitre = chapitreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + id));

        Matiere matiere = matiereRepository.findByNom(dto.getMatiere())
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec le nom: " + dto.getMatiere()));

        // Mise à jour des champs de base
        existingChapitre.setNom(dto.getTitre());
        existingChapitre.setNiveau(dto.getNiveau());
        existingChapitre.setObjectif(dto.getObjectif());
        existingChapitre.setMatiere(matiere);

        // NOTE: La gestion des sections doit être améliorée pour l'UPDATE.
        // Ici, on ne fait que la mise à jour des champs principaux.
        // Pour une gestion complète des sections (ajout/suppression/modification),
        // il faudrait une logique de "diffing" des listes.
        // Pour l'instant, nous allons ignorer les sections dans l'update pour ne pas
        // risquer de supprimer des données sans un DTO d'update plus précis.

        Chapitre chapitreSauvegarde = chapitreRepository.save(existingChapitre);
        return new ChapitreDetailDto(chapitreSauvegarde);
    }

    /**
     * Supprime un chapitre par son ID.
     */
    @Transactional
    public void deleteChapitre(Long id) {
        if (!chapitreRepository.existsById(id)) {
            throw new EntityNotFoundException("Chapitre non trouvé avec l'ID: " + id);
        }
        chapitreRepository.deleteById(id);
    }
    
    // --- Ancienne méthode creerChapitre (pour référence) ---
    

    /**
     * Crée un nouveau chapitre et ses sections associées à partir d'un DTO.
     * L'annotation @Transactional garantit que toutes les opérations réussissent
     * ou sont annulées si une erreur survient.
     *
     * MODIFICATION : Cette méthode retourne maintenant un ChapitreDetailDto pour éviter
     * les problèmes de sérialisation circulaire avec les entités.
     *
     * @param dto Le DTO contenant les informations du formulaire de création.
     * @return Un DTO représentant le chapitre qui vient d'être créé.
     * @throws RuntimeException si la matière spécifiée dans le DTO n'est pas trouvée.
     */
    @Transactional
    public ChapitreDetailDto creerChapitre(ChapitreCreateDto dto) {
        // 1. Récupérer l'entité Matiere à partir du nom fourni dans le DTO.
        Matiere matiere = matiereRepository.findByNom(dto.getMatiere())
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec le nom : " + dto.getMatiere()));

        // 2. Créer une nouvelle instance de l'entité Chapitre.
        Chapitre nouveauChapitre = new Chapitre();

        // 3. Remplir les champs de l'entité à partir des données du DTO.
        nouveauChapitre.setNom(dto.getTitre());
        nouveauChapitre.setNiveau(dto.getNiveau());
        nouveauChapitre.setObjectif(dto.getObjectif());
        nouveauChapitre.setMatiere(matiere); // Associer la matière trouvée.

        // 4. Créer les entités Section et les lier au nouveau chapitre.
        if (dto.getSections() != null && !dto.getSections().isEmpty()) {
            List<Section> sections = dto.getSections().stream()
                .map(sectionDto -> {
                    Section section = new Section();
                    section.setTitre(sectionDto.getTitre());
                    section.setContenu(sectionDto.getContenu()); // Remplir le contenu de la section
                    section.setChapitre(nouveauChapitre); // Lier chaque section au chapitre parent.
                    return section;
                })
                .collect(Collectors.toList());
            nouveauChapitre.setSections(sections);
        }

        // 5. Sauvegarder l'entité Chapitre (et ses sections en cascade).
        Chapitre chapitreSauvegarde = chapitreRepository.save(nouveauChapitre);

        // 6. Convertir l'entité sauvegardée en DTO de réponse et le retourner.
        //    C'est l'étape cruciale qui résout l'erreur.
        return new ChapitreDetailDto(chapitreSauvegarde);
    }
}
