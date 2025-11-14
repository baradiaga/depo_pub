// Fichier : src/app/services/parcours.service.ts (Version finale et corrigée)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
// On importe le DTO qui représente la réponse de l'API
import { ParcoursDto } from '../dto/parcours.dto'; // Assurez-vous que ce chemin est correct

// On peut garder cette interface si elle est utilisée ailleurs, sinon elle peut être supprimée.
export interface Parcours {
  id: number;
  nom: string;
  dateCreation: string;
  chapitres: any[]; // Type 'any' pour la flexibilité
}

// Interface pour le payload d'enregistrement
export interface ParcoursRequestPayload {
  chapitresChoisisIds: number[];
}

@Injectable({
  providedIn: 'root'
} )
export class ParcoursService {
  private apiUrl = 'http://localhost:8080/api/parcours';

  constructor(private http: HttpClient ) { }

  enregistrerParcours(chapitreIds: number[]): Observable<any> {
    const payload: ParcoursRequestPayload = { chapitresChoisisIds: chapitreIds };
    return this.http.post<any>(`${this.apiUrl}/etudiant`, payload );
  }

  /**
   * Récupère les parcours de l'étudiant actuellement authentifié.
   */
  getMesParcours(): Observable<ParcoursDto> { // <--- CORRECTION APPLIQUÉE ICI
    // ====================================================================
    // === LA SIGNATURE EST MAINTENANT CORRECTE                         ===
    // ====================================================================
    // La méthode promet maintenant de retourner un objet de type ParcoursDto,
    // ce qui correspond à la réalité de l'API.
    return this.http.get<ParcoursDto>(`${this.apiUrl}/etudiant/me` );
  }
}
