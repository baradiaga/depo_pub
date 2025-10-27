// Fichier : src/app/services/element-constitutif.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// L'import de 'environment' est supprimé

export interface ElementConstitutif {
  id?: number;
  nom: string;
  code: string;
  description: string;
  credit: number;
  enseignantId: number | null;
  enseignantNom?: string;
}

@Injectable({
  providedIn: 'root'
} )
export class ElementConstitutifService {
  // =======================================================
  // === URL DE L'API MISE EN DUR ICI ===
  // =======================================================
  private baseUrl = 'http://localhost:8080/api'; // Adaptez si votre port ou base est différent

  constructor(private http: HttpClient ) { }

  getElementsForUnite(ueId: number): Observable<ElementConstitutif[]> {
    return this.http.get<ElementConstitutif[]>(`${this.baseUrl}/unites-enseignement/${ueId}/elements-constitutifs` );
  }

  create(ueId: number, element: ElementConstitutif): Observable<ElementConstitutif> {
    return this.http.post<ElementConstitutif>(`${this.baseUrl}/unites-enseignement/${ueId}/elements-constitutifs`, element );
  }

  update(id: number, element: ElementConstitutif): Observable<ElementConstitutif> {
    return this.http.put<ElementConstitutif>(`${this.baseUrl}/elements-constitutifs/${id}`, element );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/elements-constitutifs/${id}` );
  }
}
