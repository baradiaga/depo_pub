// Fichier : src/app/services/progression.service.ts (Version Simplifiée et Corrigée)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// On importe l'interface standard depuis le fichier central
import { ElementConstitutifResponse } from './models';

@Injectable({
  providedIn: 'root'
} )
export class ProgressionService {
  // L'URL pointe vers l'endpoint qui renvoie les matières de l'utilisateur connecté
  private apiUrl = 'http://localhost:8080/api/utilisateurs/mes-inscriptions';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste des matières (EC) auxquelles l'étudiant connecté est inscrit.
   */
  getMesMatieresInscrites(): Observable<ElementConstitutifResponse[]> {
    return this.http.get<ElementConstitutifResponse[]>(this.apiUrl );
  }
}
