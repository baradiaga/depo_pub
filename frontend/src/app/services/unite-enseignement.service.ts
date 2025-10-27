// Fichier : src/app/services/unite-enseignement.service.ts

import { Injectable } from '@angular/core'; // <-- L'import pour le décorateur
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// L'interface reste la même
export interface UniteEnseignement {
  id?: number;
  nom: string;
  code: string;
  description: string;
  credit: number;
  semestre: number;
  responsable?: { id: number, nom: string, prenom: string };
  objectifs: string;
}

// ==========================================================
// === CORRECTION : AJOUT DU DÉCORATEUR @Injectable ===
@Injectable({
  providedIn: 'root'
} )
// ==========================================================
export class UniteEnseignementService {
  private apiUrl = 'http://localhost:8080/api/unites-enseignement';

  constructor(private http: HttpClient ) { }

  getAll(): Observable<UniteEnseignement[]> {
    return this.http.get<UniteEnseignement[]>(this.apiUrl );
  }

  create(ue: UniteEnseignement): Observable<UniteEnseignement> {
    return this.http.post<UniteEnseignement>(this.apiUrl, ue );
  }

  update(id: number, ue: UniteEnseignement): Observable<UniteEnseignement> {
    return this.http.put<UniteEnseignement>(`${this.apiUrl}/${id}`, ue );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}` );
  }
}
