package com.moscepa.dto;

import java.util.List;

/**
 * DTO principal pour le tableau de bord des tests de l'étudiant.
 * Contient la liste des matières, chapitres et tests associés, ainsi que les scores.
 */
public class EtudiantTestsDashboardDto {

    private Long etudiantId;
    private String nomComplet;
    private List<MatiereDashboardDto> matieres;

    // Constructeur, Getters et Setters
    public EtudiantTestsDashboardDto(Long etudiantId, String nomComplet, List<MatiereDashboardDto> matieres) {
        this.etudiantId = etudiantId;
        this.nomComplet = nomComplet;
        this.matieres = matieres;
    }

    public Long getEtudiantId() { return etudiantId; }
    public String getNomComplet() { return nomComplet; }
    public List<MatiereDashboardDto> getMatieres() { return matieres; }

    // --- DTO pour une Matière ---
    public static class MatiereDashboardDto {
        private Long id;
        private String nom;
        private String statutInscription;
        private List<ChapitreDashboardDto> chapitres;

        public MatiereDashboardDto(Long id, String nom, String statutInscription, List<ChapitreDashboardDto> chapitres) {
            this.id = id;
            this.nom = nom;
            this.statutInscription = statutInscription;
            this.chapitres = chapitres;
        }

        public Long getId() { return id; }
        public String getNom() { return nom; }
        public String getStatutInscription() { return statutInscription; }
        public List<ChapitreDashboardDto> getChapitres() { return chapitres; }
    }

    // --- DTO pour un Chapitre ---
    public static class ChapitreDashboardDto {
        private Long id;
        private String nom;
        private Integer niveau;
        private List<TestDashboardDto> tests;

        public ChapitreDashboardDto(Long id, String nom, Integer niveau, List<TestDashboardDto> tests) {
            this.id = id;
            this.nom = nom;
            this.niveau = niveau;
            this.tests = tests;
        }

        public Long getId() { return id; }
        public String getNom() { return nom; }
        public Integer getNiveau() { return niveau; }
        public List<TestDashboardDto> getTests() { return tests; }
    }

    // --- DTO pour un Test ---
    public static class TestDashboardDto {
        private Long id;
        private String titre;
        private Double meilleurScore; // Le meilleur score obtenu par l'étudiant pour ce test

        public TestDashboardDto(Long id, String titre, Double meilleurScore) {
            this.id = id;
            this.titre = titre;
            this.meilleurScore = meilleurScore;
        }

        public Long getId() { return id; }
        public String getTitre() { return titre; }
        public Double getMeilleurScore() { return meilleurScore; }
    }
}

