import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Utilisation d'une Enum pour plus de sécurité sur les rôles
export enum UserRole {
  ADMIN = 'ADMIN',
  ETUDIANT = 'ETUDIANT',
  ENSEIGNANT = 'ENSEIGNANT',
  TUTEUR = 'TUTEUR',
  TECHNOPEDAGOGUE = 'TECHNOPEDAGOGUE',
  RESPONSABLE_FORMATION = 'RESPONSABLE_FORMATION'
}

export interface Utilisateur {
  id?: number; // Changé en number pour correspondre au Long Java
  nom: string;
  prenom: string;
  email: string;
  motDePasse?: string; // Optionnel car non renvoyé par UserResponseDto
  role: UserRole;
  enabled?: boolean; // Correspond à l'état actif/inactif
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.apiUrl}/admin/users`;

  constructor(private http: HttpClient) {}

  getUsers(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(this.apiUrl);
  }

  addUser(user: Utilisateur): Observable<any> {
    return this.http.post<any>(this.apiUrl, user);
  }

  updateUser(id: number, user: Utilisateur): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // --- Nouvelles méthodes basées sur votre Controller Java ---

  activateUser(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/activate`, {});
  }

  deactivateUser(id: number): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/deactivate`, {});
  }

  searchUsers(query: string): Observable<Utilisateur[]> {
    const params = new HttpParams().set('q', query);
    return this.http.get<Utilisateur[]>(`${this.apiUrl}/search`, { params });
  }

  getStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats`);
  }
}
