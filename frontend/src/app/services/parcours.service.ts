// Fichier : src/app/services/parcours.service.ts (Correct)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chapitre } from './models';
export interface Parcours {
  id: number;
  nom: string;
  dateCreation: string;
  chapitres: Chapitre[];
}

@Injectable({
  providedIn: 'root'
} )
export class ParcoursService {
  private apiUrl = 'http://localhost:8080/api/parcours';

  constructor(private http: HttpClient ) { }

  /**
   * Enregistre un nouveau parcours personnalisé pour l'étudiant authentifié.
   */
  enregistrerParcours(chapitreIds: number[]): Observable<any> {
    const payload = { chapitreIds: chapitreIds };
    return this.http.post<any>(`${this.apiUrl}/etudiant`, payload );
  }

  /**
   * Récupère les parcours de l'étudiant actuellement authentifié.
   */
  getMesParcours(): Observable<Parcours[]> {
    return this.http.get<Parcours[]>(`${this.apiUrl}/etudiant/mes-parcours` );
  }
}
