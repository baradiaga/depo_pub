// Fichier : src/app/services/syllabus.service.ts (Nouveau Fichier)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- CONTRAT DE DONNÉES POUR LE SYLLABUS ---
export interface ChapitreSyllabus {
  id: number;
  nom: string;
  ordre: number;
  statut: string; // "Validé", "À faire", etc.
  dernierScore: number | null;
}

export interface MatiereSyllabus {
  id: number;
  nom: string;
  code: string;
  description: string;
  chapitres: ChapitreSyllabus[];
}

@Injectable({
  providedIn: 'root'
} )
export class SyllabusService {
  // On utilise un nouvel endpoint dédié
  private apiUrl = 'http://localhost:8080/api/syllabus';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère le syllabus d'une matière pour l'étudiant connecté.
   * @param matiereId L'ID de la matière (ElementConstitutif).
   */
  getSyllabusPourEtudiant(matiereId: number): Observable<MatiereSyllabus> {
    console.log(`[SyllabusService] Appel de l'API pour le syllabus de la matière ID : ${matiereId}`);
    // L'URL finale sera : GET /api/syllabus/matiere/15
    return this.http.get<MatiereSyllabus>(`${this.apiUrl}/matiere/${matiereId}` );
  }
}
