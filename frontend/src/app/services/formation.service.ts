import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FormationCreation, FormationDetail, RessourcePedagogique } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class FormationService {
  private apiUrl = 'http://localhost:8080/api/formations';

  constructor(private http: HttpClient) { }

  // Create
  creerFormation(formation: FormationCreation): Observable<FormationDetail> {
    return this.http.post<FormationDetail>(this.apiUrl, formation);
  }

  // Read all
  getAllFormations(): Observable<FormationDetail[]> {
    return this.http.get<FormationDetail[]>(this.apiUrl);
  }

  // Read one
  getFormationById(id: number): Observable<FormationDetail> {
    return this.http.get<FormationDetail>(`${this.apiUrl}/${id}`);
  }

  // Update
  modifierFormation(id: number, formation: FormationCreation): Observable<FormationDetail> {
    return this.http.put<FormationDetail>(`${this.apiUrl}/${id}`, formation);
  }

  // Delete
  supprimerFormation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- Documents related to a formation ---
  // Upload a file for a formation
  uploadDocument(formationId: number, fichier: File): Observable<RessourcePedagogique> {
    const formData = new FormData();
    formData.append('file', fichier, fichier.name);
    return this.http.post<RessourcePedagogique>(`${this.apiUrl}/${formationId}/documents`, formData);
  }

  // Get documents for a formation
  getDocuments(formationId: number): Observable<RessourcePedagogique[]> {
    return this.http.get<RessourcePedagogique[]>(`${this.apiUrl}/${formationId}/documents`);
  }

  // Delete a document
  supprimerDocument(formationId: number, docId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${formationId}/documents/${docId}`);
  }

  // --- Intervenants (enseignants) management ---
  getIntervenants(): Observable<any[]> {
    // Endpoint hypothétique retournant la liste des utilisateurs/enseignants
    return this.http.get<any[]>(`http://localhost:8080/api/utilisateurs/intervenants`);
  }

  // Attach an intervenant to a formation
  ajouterIntervenant(formationId: number, userId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${formationId}/intervenants`, { userId });
  }

  // Detach an intervenant
  retirerIntervenant(formationId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${formationId}/intervenants/${userId}`);
  }

  // Optional: search formations (pagination/filtre)
  searchFormations(query?: string, page?: number, size?: number): Observable<FormationDetail[]> {
    let params = new HttpParams();
    if (query) params = params.set('q', query);
    if (page != null) params = params.set('page', String(page));
    if (size != null) params = params.set('size', String(size));
    return this.http.get<FormationDetail[]>(`${this.apiUrl}/search`, { params });
  }
}
