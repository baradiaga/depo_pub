import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// Modèles DTO supposés basés sur l'analyse du backend
// Nous allons utiliser des interfaces génériques pour l'instant
export interface Formation {
  id: number;
  nom: string;
  code: string;
  description?: string;
  statut?: string; // StatutFormation
  niveauEtude?: string; // NiveauEtude
  dureeAnnees?: number;
  objectifs?: string;
  prerequis?: string;
  debouches?: string;
  evaluationModalites?: string;
  modaliteEnseignement?: string;
  lieu?: string;
  dateDebut?: string;
  dateFin?: string;
  capacite?: number;
  tarif?: number;
  certificationProfessionnelle?: string;
  volumeHoraireTotal?: number;
  ectsTotal?: number;
  competences?: { id: number; nom: string }[];
  unitesEnseignement?: { id: number; nom: string }[];
  responsableId?: number;
  responsableNom?: string;
  createurId?: number;
  createurNom?: string;
  nbEtudiants?: number; // calculable côté backend ou frontend
  elementsConstitutifs?: { id: number; nom: string; code: string }[];
}

export interface StudentJourney {
  id: number;
  etudiantNom: string;
  formationId?: number;     // Ajouté pour corriger l'erreur
  formationNom?: string;    // Optionnel, utile pour afficher le nom du cours
  progression?: number;
}

@Injectable({
  providedIn: 'root'
})
export class EnseignantService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  /**
   * Récupère la liste des formations/cours de l'enseignant.
   * NOTE: Cet endpoint n'existe pas encore et sera créé dans la phase 7.
   * Pour l'instant, on suppose qu'il s'appelle /api/formations/mes-formations
   */
  getMesFormations(): Observable<Formation[]> {
    return this.http.get<Formation[]>(`${this.apiUrl}/formations/mes-formations`);
  }

  /**
   * Récupère la liste des parcours étudiants pour l'évaluation.
   * On utilise l'endpoint existant /api/student-journey/all.
   */
  getAllStudentJourneys(): Observable<StudentJourney[]> {
    return this.http.get<StudentJourney[]>(`${this.apiUrl}/student-journey/mes-etudiants`);
  }

  /**
   * Récupère le parcours détaillé d'un étudiant.
   */
  getStudentJourney(studentId: number): Observable<StudentJourney> {
    return this.http.get<StudentJourney>(`${this.apiUrl}/student-journey/${studentId}`);
  }
}
