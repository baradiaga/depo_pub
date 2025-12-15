package com.moscepa.dto;

public class InscriptionResponseDto {

    private Long id;
    private String statut;
    private boolean actif;
    private String dateInscription;
    private String dateValidation;

    // --- Étudiant ---
    private Long etudiantId;
    private String etudiantNomComplet;
    private String etudiantEmail;

    // --- Matière (EC) ---
    private Long ecId;
    private String ecCode;
    private String ecNom;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    public String getDateInscription() { return dateInscription; }
    public void setDateInscription(String dateInscription) { this.dateInscription = dateInscription; }

    public String getDateValidation() { return dateValidation; }
    public void setDateValidation(String dateValidation) { this.dateValidation = dateValidation; }

    public Long getEtudiantId() { return etudiantId; }
    public void setEtudiantId(Long etudiantId) { this.etudiantId = etudiantId; }

    public String getEtudiantNomComplet() { return etudiantNomComplet; }
    public void setEtudiantNomComplet(String etudiantNomComplet) { this.etudiantNomComplet = etudiantNomComplet; }

    public String getEtudiantEmail() { return etudiantEmail; }
    public void setEtudiantEmail(String etudiantEmail) { this.etudiantEmail = etudiantEmail; }

    public Long getEcId() { return ecId; }
    public void setEcId(Long ecId) { this.ecId = ecId; }

    public String getEcCode() { return ecCode; }
    public void setEcCode(String ecCode) { this.ecCode = ecCode; }

    public String getEcNom() { return ecNom; }
    public void setEcNom(String ecNom) { this.ecNom = ecNom; }
}
