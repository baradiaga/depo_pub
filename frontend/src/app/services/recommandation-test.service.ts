import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';

// --- INTERFACES (à ajuster si les DTOs du backend sont différents ) ---
export interface ChapitreTest {
  id: number;
  numero: string;
  nom: string;
  resultat?: number;
  categorie?: string;
  actionValidee: boolean;
}

export interface ElementConstitutif {
  id: number;
  code: string;
  nom: string;
  credit: number;
  description: string;
  chapitres: ChapitreTest[];
  enseignant?: { id: number; nom: string; prenom: string; };
}

export interface MatiereSelection {
  id: number;
  titreComplet: string;
}

@Injectable({
  providedIn: 'root'
})
export class RecommandationTestService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient ) { }

  getListeMatieres(): Observable<MatiereSelection[]> {
    return this.http.get<ElementConstitutif[]>(`${this.apiUrl}/elements-constitutifs` ).pipe(
      map(elements =>
        elements.map(ec => ({
          id: ec.id,
          titreComplet: `${ec.code}: ${ec.nom}`
        }))
      )
    );
  }

  getDetailsEC(ecId: number): Observable<ElementConstitutif | undefined> {
    if (!ecId) {
      return of(undefined);
    }
    // Temporaire : en attendant GET /api/elements-constitutifs/{id}
    return this.http.get<ElementConstitutif[]>(`${this.apiUrl}/elements-constitutifs` ).pipe(
        map(elements => elements.find(el => el.id === ecId))
    );
  }
}
