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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service contenant la logique métier pour la gestion des chapitres.
 */
@Service
public class ChapitreService {

    private final ChapitreRepository chapitreRepository;
    private final MatiereRepository matiereRepository;

    @Autowired
    public ChapitreService(ChapitreRepository chapitreRepository, MatiereRepository matiereRepository) {
        this.chapitreRepository = chapitreRepository;
        this.matiereRepository = matiereRepository;
    }

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
