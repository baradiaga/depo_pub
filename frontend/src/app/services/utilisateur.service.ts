// Fichier : src/app/services/utilisateur.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// On renomme l'interface pour correspondre au DTO du backend.
// C'est une bonne pratique pour la clarté.
export interface UserResponseDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role?: string;
  actif?: boolean;
}

@Injectable({
  providedIn: 'root'
} )
export class UtilisateurService {
  
  // L'URL de base de l'API.
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient ) { }

  // =======================================================
  // === MÉTHODE CORRIGÉE                                  ===
  // =======================================================
  /**
   * Récupère la liste des utilisateurs ayant le rôle 'ENSEIGNANT'.
   * Appelle la nouvelle route sécurisée que nous avons créée.
   * @returns Un Observable contenant un tableau de DTOs d'utilisateurs.
   */
  getEnseignants(): Observable<UserResponseDto[]> {
    // On appelle la nouvelle URL : /api/users/enseignants
    return this.http.get<UserResponseDto[]>(`${this.apiUrl}/users/enseignants` );
  }

  // Vous pouvez ajouter ici d'autres méthodes pour gérer les utilisateurs si nécessaire
  // Par exemple :
  // getAllUsers(): Observable<UserResponseDto[]> {
  //   return this.http.get<UserResponseDto[]>(`${this.apiUrl}/users` );
  // }
}
