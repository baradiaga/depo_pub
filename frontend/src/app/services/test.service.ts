// Fichier : src/app/services/test.service.ts (Corrigé et Complété)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ====================================================================
// === INTERFACES EXPORTÉES                                         ===
// ====================================================================

// Représente une option de réponse pour une question
export interface Reponse {
  id: number;
  texte: string;
  correcte: boolean;
}

// Représente une question dans un test
export interface Question {
  id: number;
  enonce: string;
  type: 'QCU' | 'QCM' | 'VRAI_FAUX' | 'TEXTE_LIBRE';
  points: number;
  reponses: Reponse[];
}

// Représente un test complet avec ses questions
export interface Test {
  id: number;
  titre: string;
  description?: string;
  chapitreId: number;
  questions: Question[]; // La liste des questions est maintenant une propriété de Test
}

@Injectable({
  providedIn: 'root'
} )
export class TestService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste de tous les tests associés à un chapitre spécifique.
   * Cette méthode est celle que le composant va utiliser.
   */
  getTestsByChapitre(chapitreId: number): Observable<Test[]> {
    // Note: Assurez-vous que cet endpoint existe dans votre backend.
    return this.http.get<Test[]>(`${this.apiUrl}/chapitres/${chapitreId}/tests` );
  }
}
