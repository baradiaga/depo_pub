/**
 * Interfaces pour le tableau de bord des tests de l'étudiant.
 * Correspond au DTO Java EtudiantTestsDashboardDto.
 */

export interface TestDashboard {
    id: number;
    titre: string;
    meilleurScore: number; // Le meilleur score obtenu par l'étudiant pour ce test
}

export interface ChapitreDashboard {
    id: number;
    nom: string;
    niveau: number;
    tests: TestDashboard[];
}

export interface MatiereDashboard {
    id: number;
    nom: string;
    statutInscription: string;
    chapitres: ChapitreDashboard[];
}

export interface EtudiantTestsDashboard {
    etudiantId: number;
    nomComplet: string;
    matieres: MatiereDashboard[];
}

