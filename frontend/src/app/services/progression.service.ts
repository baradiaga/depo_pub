// Fichier : src/app/services/progression.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Nouvelle interface qui correspond au DTO du backend
export interface MatiereInscrite {
  id: number;
  nomEc: string;
  codeEc: string;
  nomUe: string;
  codeUe: string;
  coefficient: number;
  statut: string;
}

@Injectable({ providedIn: 'root' } )
export class ProgressionService {
  private apiUrl = 'http://localhost:8080/api/users/mes-inscriptions';

  constructor(private http: HttpClient ) { }

  getMesMatieres(): Observable<MatiereInscrite[]> {
    return this.http.get<MatiereInscrite[]>(this.apiUrl );
  }
}
