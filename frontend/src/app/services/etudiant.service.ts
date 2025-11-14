// Fichier : src/app/services/etudiant.service.ts (Version finale avec CRUD complet)

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Définissez vos interfaces ici ou importez-les
export interface EtudiantPayload {
  id?: number; // L'ID est optionnel (présent pour la mise à jour )
  nom: string;
  prenom: string;
  email: string;
  motDePasse?: string; // Le mot de passe est optionnel pour la mise à jour
  // ... incluez tous les autres champs de votre formulaire
  dateDeNaissance: string;
  lieuDeNaissance: string;
  nationalite: string;
  sexe: string;
  adresse: string;
  telephone: string;
  anneeAcademique: string;
  filiere: string;
  matiereIds?: number[];
}

export interface EtudiantDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class EtudiantService {
  private adminUsersApiUrl = 'http://localhost:8080/api/admin/users';
  private etudiantsApiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient ) { }

  // --- READ (Lecture) ---
  getEtudiants(): Observable<EtudiantDto[]> {
    const url = `${this.adminUsersApiUrl}/role/ETUDIANT`;
    return this.http.get<EtudiantDto[]>(url );
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR LE MODE ÉDITION                        ===
  // ====================================================================
  /**
   * Récupère les informations complètes d'un utilisateur par son ID.
   */
  getEtudiantById(id: number): Observable<EtudiantPayload> {
    // Cet endpoint doit exister dans votre AdminController.java
    return this.http.get<EtudiantPayload>(`${this.adminUsersApiUrl}/${id}` );
  }

  // --- CREATE (Création) ---
  inscrireNouvelEtudiant(payload: EtudiantPayload): Observable<any> {
    return this.http.post(`${this.etudiantsApiUrl}/inscrire`, payload );
  }

  // ====================================================================
  // === NOUVELLE MÉTHODE POUR LA MISE À JOUR                         ===
  // ====================================================================
  /**
   * Met à jour les informations d'un étudiant existant.
   */
  updateEtudiant(id: number, payload: EtudiantPayload): Observable<any> {
    // Cet endpoint doit exister dans votre AdminController.java
    return this.http.put(`${this.adminUsersApiUrl}/${id}`, payload );
  }

  // --- DELETE (Suppression) ---
  deleteEtudiant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.adminUsersApiUrl}/${id}` );
  }
}
