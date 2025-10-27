import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface pour les données que nous allons recevoir
export interface MatiereStatut {
  matiereId: number;
  ue: string;
  ordre: number;
  ec: string;
  coefficient: number;
  statut: string;
}

@Injectable({
  providedIn: 'root'
} )
export class ProgressionService {
  private apiUrl = 'http://localhost:8080/api/progression';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste des matières et leur statut pour l'étudiant connecté.
   */
  getMesMatieres(): Observable<MatiereStatut[]> {
    return this.http.get<MatiereStatut[]>(`${this.apiUrl}/mes-matieres` );
  }
}
