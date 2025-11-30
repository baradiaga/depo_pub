import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Categorie {
  id?: number;
  nom: string;
  echelleId: number;
  echelleIntervalle?: string; // optionnel pour affichage
}

@Injectable({
  providedIn: 'root'
})
export class CategorieService {
  private apiUrl = 'http://localhost:8080/api/categories'; // URL backend

  constructor(private http: HttpClient) { }

  // GET: toutes les catégories
  getAll(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(this.apiUrl);
  }

  // GET: catégorie par id
  getById(id: number): Observable<Categorie> {
    return this.http.get<Categorie>(`${this.apiUrl}/${id}`);
  }

  // POST: créer une catégorie
  create(categorie: Categorie): Observable<Categorie> {
    return this.http.post<Categorie>(this.apiUrl, categorie);
  }

  // PUT: mettre à jour une catégorie
  update(id: number, categorie: Categorie): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.apiUrl}/${id}`, categorie);
  }

  // DELETE: supprimer une catégorie
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
