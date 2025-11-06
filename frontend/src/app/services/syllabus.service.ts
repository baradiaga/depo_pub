// Fichier : src/app/services/syllabus.service.ts (Version Complète et Finale)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- INTERFACES (Le "Contrat" avec le Backend ) ---

// Interface pour un chapitre dans le tableau du syllabus
export interface ChapitreSyllabus {
  id: number;
  nom: string;
  ordre: number;
  nomTest: string;
  resultatScore: number | null;
  categorie: string;
}

// Interface pour la page complète du syllabus
export interface MatiereSyllabus {
  id: number;
  nom: string;
  code: string;
  description: string;
  chapitres: ChapitreSyllabus[];
}

@Injectable({
  providedIn: 'root'
})
export class SyllabusService {
  // L'URL de base pour l'API du syllabus
  private apiUrl = 'http://localhost:8080/api/syllabus';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère le syllabus complet d'une matière pour l'étudiant connecté.
   * @param matiereId L'ID de la matière (ElementConstitutif) à récupérer.
   */
  getSyllabusPourEtudiant(matiereId: number): Observable<MatiereSyllabus> {
    console.log(`[SyllabusService] Appel de l'API : GET ${this.apiUrl}/matiere/${matiereId}`);
    return this.http.get<MatiereSyllabus>(`${this.apiUrl}/matiere/${matiereId}` );
  }
}
