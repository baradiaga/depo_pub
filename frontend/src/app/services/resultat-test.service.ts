// Fichier : src/app/services/resultat-test.service.ts (Vérification)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// --- INTERFACES POUR LA SOUMISSION ET LA RÉCEPTION DES RÉSULTATS ---

export interface ReponseUtilisateur {
  questionId: number;
  reponseIds: number[];
  texteReponse: string | null;
}

export interface Resultat {
  id: number;
  score: number;
  scoreTotal: number;
  dateTest: string;
}

// ==========================================================
// === ASSUREZ-VOUS QUE CETTE LIGNE EST BIEN PRÉSENTE       ===
// ==========================================================
@Injectable({
  providedIn: 'root'
} )
export class ResultatTestService {

  private apiUrl = 'http://localhost:8080/api/resultats';

  constructor(private http: HttpClient ) { }

  /**
   * Soumet les réponses d'un étudiant pour un test donné.
   */
  soumettreResultat(testId: number, reponses: ReponseUtilisateur[]): Observable<Resultat> {
    return this.http.post<Resultat>(`${this.apiUrl}/test/${testId}`, reponses );
  }
}
