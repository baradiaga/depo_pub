import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

type RoleType = 'ADMIN' | 'ETUDIANT' | 'ENSEIGNANT' | 'TUTEUR' | 'TECHNOPEDAGOGUE' | 'RESPONSABLE_FORMATION' | '';
 export interface Utilisateur {
  id?: string;
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: RoleType;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/admin/users'; // adapte selon ton backend

  constructor(private http: HttpClient) {}

  // Ajouter un utilisateur
  addUser(user: Utilisateur): Observable<Utilisateur> {
    return this.http.post<Utilisateur>(this.apiUrl, user);
  }

  // Modifier un utilisateur
  updateUser(id: string, user: Partial<Utilisateur>): Observable<Utilisateur> {
    return this.http.put<Utilisateur>(`${this.apiUrl}/${id}`, user);
  }

  // Supprimer un utilisateur
  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Lister tous les utilisateurs
  getUsers(): Observable<Utilisateur[]> {
    return this.http.get<Utilisateur[]>(this.apiUrl);
  }

  // Récupérer un utilisateur par ID
  getUserById(id: string): Observable<Utilisateur> {
    return this.http.get<Utilisateur>(`${this.apiUrl}/${id}`);
  }
}
