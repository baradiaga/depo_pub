// Fichier : src/main/java/com/moscepa/dto/MatiereStatutDto.java (Ajout du constructeur)

package com.moscepa.dto;

public class MatiereStatutDto {

    private Long id;
    private String nom;
    private int ordre;
    private String ec; // code
    private int coefficient; // credit
    private String statut;

    // --- Constructeur par défaut (gardez-le) ---
    public MatiereStatutDto() {}

    // ====================================================================
    // === CONSTRUCTEUR MANQUANT À AJOUTER ICI                          ===
    // ====================================================================
    /**
     * Constructeur complet pour créer le DTO en une seule ligne.
     */
    public MatiereStatutDto(Long id, String nom, int ordre, String ec, int coefficient, String statut) {
        this.id = id;
        this.nom = nom;
        this.ordre = ordre;
        this.ec = ec;
        this.coefficient = coefficient;
        this.statut = statut;
    }

    // --- Getters et Setters (ils doivent déjà être là) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getOrdre() { return ordre; }
    public void setOrdre(int ordre) { this.ordre = ordre; }
    public String getEc() { return ec; }
    public void setEc(String ec) { this.ec = ec; }
    public int getCoefficient() { return coefficient; }
    public void setCoefficient(int coefficient) { this.coefficient = coefficient; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}
