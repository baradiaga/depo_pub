// Dans src/app/services/parcours.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// ... (vos interfaces restent inchangées )
export interface ParcoursItem {
  chapitreId: number;
  chapitreNom: string;
  matiereNom: string;
  dernierScore: number;
}

export interface ParcoursData {
  recommandes: ParcoursItem[];
  choisis: ParcoursItem[];
  mixtes: ParcoursItem[];
}

@Injectable({
  providedIn: 'root'
})
export class ParcoursService {

  private apiUrl = 'http://localhost:8080/api/parcours';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère tous les parcours pour un étudiant donné.
   * Cette méthode reste inchangée car le GET en a besoin.
   */
  getParcoursEtudiant(etudiantId: number): Observable<ParcoursData> {
    const url = `${this.apiUrl}/etudiant/${etudiantId}`;
    return this.http.get<ParcoursData>(url );
  }

  // ====================================================================
  // ===> MÉTHODE CORRIGÉE <===
  // ====================================================================
  /**
   * Enregistre les chapitres choisis par l'étudiant (actuellement connecté).
   * @param chapitreIds La liste des IDs des chapitres à enregistrer.
   * @returns Un Observable qui se complète quand l'opération est terminée.
   */
  enregistrerParcours(chapitreIds: number[]): Observable<any> {
    // CORRECTION : L'URL pour la requête POST ne doit pas contenir l'ID de l'étudiant.
    const url = `${this.apiUrl}/etudiant`;
    
    // Le payload est correct. Le nom de la clé 'chapitresChoisisIds'
    // doit correspondre au DTO Java (ParcoursRequestDto).
    const payload = { chapitresChoisisIds: chapitreIds };
    
    console.log(`[ParcoursService] Envoi de la requête POST vers ${url} avec le payload :`, payload);
    
    // L'appel HTTP POST utilise maintenant la bonne URL.
    return this.http.post(url, payload );
  }
}
