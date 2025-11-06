// Fichier : src/app/services/inscription.service.ts (Nouveau fichier)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface pour le payload de la requête
export interface InscriptionRequest {
  etudiantId: number;
  ecId: number; // ecId = Élément Constitutif ID
}

@Injectable({
  providedIn: 'root'
} )
export class InscriptionService {
  private apiUrl = 'http://localhost:8080/api/inscriptions';

  constructor(private http: HttpClient ) { }

  /**
   * Envoie une requête au backend pour inscrire un étudiant à une matière (EC).
   * @param payload Contient l'ID de l'étudiant et l'ID de l'EC.
   */
  inscrire(payload: InscriptionRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, payload );
  }

  // On pourra ajouter ici des méthodes pour lister les inscriptions, etc.
}
