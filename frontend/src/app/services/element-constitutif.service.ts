// Fichier : src/app/services/element-constitutif.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// L'import de 'environment' est supprimé

export interface EnseignantDto {
  id: number;
  nom: string;
  prenom: string;
}

export interface ElementConstitutifRequest {
  id?: number;
  nom: string;
  code: string;
  description: string;
  credit: number;
  enseignantId: number | null;
}

export interface ElementConstitutifResponse {
  id?: number;
  nom: string;
  code: string;
  description: string;
  credit: number;
  enseignant: EnseignantDto | null;
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

  getElementsForUnite(ueId: number): Observable<ElementConstitutifResponse[]> {
    return this.http.get<ElementConstitutifResponse[]>(`${this.baseUrl}/unites-enseignement/${ueId}/elements-constitutifs` );
  }

  create(ueId: number, element: ElementConstitutifRequest): Observable<ElementConstitutifResponse> {
    return this.http.post<ElementConstitutifResponse>(`${this.baseUrl}/unites-enseignement/${ueId}/elements-constitutifs`, element );
  }

  update(id: number, element: ElementConstitutifRequest): Observable<ElementConstitutifResponse> {
    return this.http.put<ElementConstitutifResponse>(`${this.baseUrl}/elements-constitutifs/${id}`, element );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/elements-constitutifs/${id}` );
  }
}
