// Fichier : src/app/services/inscription.service.ts (Nouveau fichier)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inscription, InscriptionValidationRequest } from '../models/inscription-validation.model'; // Assurez-vous que le chemin est correct

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

  /**
   * Envoie une requête au backend pour valider ou rejeter une inscription.
   * @param request Contient l'ID de l'inscription et le nouveau statut.
   */
  validerInscription(request: InscriptionValidationRequest): Observable<Inscription> {
    return this.http.post<Inscription>(`${this.apiUrl}/valider`, request);
  }

  /**
   * Récupère la liste des inscriptions en attente de validation.
   */
  getInscriptionsEnAttente(): Observable<Inscription[]> {
    // Supposons que nous allons créer un endpoint dans le backend pour cela
    // Si l'endpoint n'existe pas, il faudra le créer dans le backend.
    // Pour l'instant, on suppose qu'il existe ou qu'on le créera.
    return this.http.get<Inscription[]>(`${this.apiUrl}/en-attente`);
  }

  /**
   * Change le statut actif/inactif d'une inscription.
   * @param id L'ID de l'inscription.
   * @param actif Le statut actif souhaité (true pour activer, false pour désactiver).
   */
  changerStatutActif(id: number, actif: boolean): Observable<Inscription> {
    return this.http.put<Inscription>(`${this.apiUrl}/${id}/actif?actif=${actif}`, {});
  }
}
