// Fichier : src/app/services/test.service.ts (Version mise à jour pour l'historique)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Question, ResultatTest } from '../models/models';

// ====================================================================
// === NOUVELLE INTERFACE POUR L'HISTORIQUE DES RÉSULTATS           ===
// ====================================================================
/**
 * Représente un seul élément dans la liste de l'historique des résultats.
 * Doit correspondre au DTO `HistoriqueResultatDto.java` du backend.
 */
export interface HistoriqueResultat {
  nomChapitre: string;
  dateSoumission: string; // Les dates sont souvent transmises comme des chaînes de caractères (ISO )
  scoreObtenu: number;
  scoreTotalPossible: number;
  pourcentage: number;
}

@Injectable({
  providedIn: 'root'
})
export class TestService {

  private apiUrl = 'http://localhost:8080/api/tests';

  constructor(private http: HttpClient  ) { }

  // --- VOS MÉTHODES EXISTANTES (INCHANGÉES) ---

  getQuestionsPourChapitre(chapitreId: number): Observable<Question[]> {
    console.log(`[TestService] Appel API pour récupérer les questions du chapitre ${chapitreId}`);
    return this.http.get<Question[]>(`${this.apiUrl}/chapitre/${chapitreId}/questions`  );
  }

  soumettreReponses(chapitreId: number, reponses: any): Observable<ResultatTest> {
    console.log(`[TestService] Appel API pour soumettre les réponses du chapitre ${chapitreId}`);
    return this.http.post<ResultatTest>(`${this.apiUrl}/chapitre/${chapitreId}/soumettre`, reponses  );
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR L'HISTORIQUE DES RÉSULTATS             ===
  // ====================================================================
  /**
   * Récupère l'historique des résultats de test pour l'étudiant connecté.
   * @returns Un Observable contenant un tableau d'objets HistoriqueResultat.
   */
  getMonHistorique(): Observable<HistoriqueResultat[]> {
    // Appelle l'endpoint : GET /api/tests/mon-historique
    console.log(`[TestService] Appel API pour récupérer l'historique des résultats.`);
    return this.http.get<HistoriqueResultat[]>(`${this.apiUrl}/mon-historique` );
  }
}
