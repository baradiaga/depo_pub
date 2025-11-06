// Fichier : src/app/services/etudiant.service.ts (Version Complète et Corrigée)

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface pour le payload d'inscription complet
export interface EtudiantRegistrationPayload {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  dateDeNaissance: string;
  lieuDeNaissance: string;
  nationalite: string;
  sexe: string;
  adresse: string;
  telephone: string;
  anneeAcademique: string;
  filiere: string;
  matiereIds: number[];
}

// Interface simplifiée pour afficher une liste d'étudiants
export interface EtudiantDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
} )
export class EtudiantService {
  private usersApiUrl = 'http://localhost:8080/api/users';
  private etudiantsApiUrl = 'http://localhost:8080/api/etudiants';

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste de tous les utilisateurs ayant le rôle ETUDIANT.
   */
  getEtudiants(): Observable<EtudiantDto[]> {
    return this.http.get<EtudiantDto[]>(`${this.usersApiUrl}/etudiants` );
  }

  /**
   * Envoie les données du formulaire d'inscription complet au backend.
   */
  inscrireNouvelEtudiant(payload: EtudiantRegistrationPayload): Observable<any> {
    return this.http.post(`${this.etudiantsApiUrl}/inscrire`, payload );
  }

  /**
   * Supprime un étudiant par son ID.
   * NOTE : Cette méthode supprime l'UTILISATEUR, pas juste une inscription.
   * Elle devrait appeler l'API des utilisateurs.
   */
  deleteEtudiant(id: number): Observable<void> {
    return this.http.delete<void>(`${this.usersApiUrl}/${id}` );
  }

  // --- Les autres méthodes peuvent être ajoutées ici au besoin ---
}
