// Fichier : src/main/java/com/moscepa/dto/SectionDto.java (Version Classe, plus flexible)

package com.moscepa.dto;

import com.moscepa.entity.Section;
import com.moscepa.entity.TypeSection; // Assurez-vous que cet import est là

public class SectionDto {

    private Long id;
    private String titre;
    private String contenu;
    private Integer ordre;
    private TypeSection typeSection; // <-- APPORT : Le type de la section
    private Long chapitreId; // Conservé pour la compatibilité

    // Constructeur vide requis par de nombreux frameworks
    public SectionDto() {}

    // Constructeur pour la conversion depuis l'entité (le plus courant)
    public SectionDto(Section section) {
        this.id = section.getId();
        this.titre = section.getTitre();
        this.contenu = section.getContenu();
        this.ordre = section.getOrdre();
        this.typeSection = section.getTypeSection(); // On récupère le nouveau champ
        if (section.getChapitre() != null) {
            this.chapitreId = section.getChapitre().getId();
        }
    }
    
    // Constructeur utilisé par votre SectionService (conservé pour la compatibilité)
    public SectionDto(Long id, String titre, String contenu, Integer ordre, Long chapitreId) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.ordre = ordre;
        this.chapitreId = chapitreId;
        // On peut laisser typeSection à null ou lui donner une valeur par défaut
        this.typeSection = TypeSection.TEXTE; 
    }

    // --- Getters et Setters ---
    // (Générez tous les getters et setters pour les 6 champs ci-dessus)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public Integer getOrdre() { return ordre; }
    public void setOrdre(Integer ordre) { this.ordre = ordre; }
    public TypeSection getTypeSection() { return typeSection; }
    public void setTypeSection(TypeSection typeSection) { this.typeSection = typeSection; }
    public Long getChapitreId() { return chapitreId; }
    public void setChapitreId(Long chapitreId) { this.chapitreId = chapitreId; }
}
