// Fichier : src/app/admin/pages/services/permissions.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// DTOs pour le frontend
export interface FonctionnaliteDTO {
  id: number;
  nom: string;
  featureKey: string;
  icon: string;
  sousFonctionnalites: FonctionnaliteDTO[];
}

// Définition des actions CRUD
export type PermissionAction = 'lister' | 'lire' | 'creer' | 'modifier' | 'supprimer';

// Structure pour la matrice de permissions
export interface PermissionMatrix {
  [resource: string]: {
    [action in PermissionAction]: boolean;
  };
}

// Map: { [roleName: string]: string[] (featureKeys ) }
export interface RolePermissionsMap {
  [roleName: string]: string[];
}

@Injectable({
  providedIn: 'root'
})
export class PermissionsService {
  private apiUrl = 'http://localhost:8080/api/permissions';
  private fonctionnalitesUrl = 'http://localhost:8080/api/fonctionnalites';
  private rolesUrl = 'http://localhost:8080/api/admin/roles'; 

  constructor(private http: HttpClient ) { }

  /**
   * Récupère la liste de toutes les fonctionnalités (featureKeys disponibles).
   */
  getAllFonctionnalites(): Observable<FonctionnaliteDTO[]> {
    return this.http.get<FonctionnaliteDTO[]>(this.fonctionnalitesUrl );
  }

  /**
   * Récupère la liste de tous les rôles.
   */
  getAllRoles(): Observable<string[]> {
    return new Observable(observer => {
      observer.next([
        'ADMIN',
        'ETUDIANT',
        'ENSEIGNANT',
        'TUTEUR',
        'TECHNOPEDAGOGUE',
        'RESPONSABLE_FORMATION'
      ]);
      observer.complete();
    });
  }

  /**
   * Extrait les ressources (tables) disponibles.
   */
  getAvailableResources(): string[] {
    // Liste des tables/ressources basées sur l'analyse des contrôleurs et entités
    return [
      'utilisateur',
      'role',
      'permission',
      'matiere', // ElementConstitutif
      'chapitre',
      'questionnaire',
      'test',
      'ressource',
      'formation',
      'inscription',
      'parcours',
      'fonctionnalite'
    ];
  }

  /**
   * Convertit la liste de featureKeys du backend (ex: ['utilisateur:lire', 'utilisateur:creer'])
   * en une matrice de permissions pour le frontend.
   */
  featureKeysToMatrix(featureKeys: string[]): PermissionMatrix {
    const matrix: PermissionMatrix = {};
    const resources = this.getAvailableResources();
    const actions: PermissionAction[] = ['lister', 'lire', 'creer', 'modifier', 'supprimer'];

    resources.forEach(resource => {
      matrix[resource] = {} as any;
      actions.forEach(action => {
        const key = `${resource}:${action}`;
        // La permission est accordée si la clé existe dans la liste du backend
        matrix[resource][action] = featureKeys.includes(key);
      });
    });

    return matrix;
  }

  /**
   * Convertit la matrice de permissions du frontend en une liste de featureKeys
   * pour l'envoi au backend.
   */
  matrixToFeatureKeys(matrix: PermissionMatrix): string[] {
    const featureKeys: string[] = [];
    const resources = this.getAvailableResources();
    const actions: PermissionAction[] = ['lister', 'lire', 'creer', 'modifier', 'supprimer'];

    resources.forEach(resource => {
      actions.forEach(action => {
        // Si la case est cochée dans la matrice
        if (matrix[resource] && matrix[resource][action]) {
          featureKeys.push(`${resource}:${action}`);
        }
      });
    });

    return featureKeys;
  }

  /**
   * Récupère toutes les permissions par rôle.
   * Retourne un Map<String, Set<String>> du backend.
   */
  getAllPermissionsByRole(): Observable<RolePermissionsMap> {
    return this.http.get<RolePermissionsMap>(this.apiUrl );
  }

  /**
   * Met à jour les permissions pour un rôle donné.
   * @param roleName Le nom du rôle (ex: 'ETUDIANT').
   * @param matrix La matrice de permissions du frontend.
   */
  updatePermissionsForRole(roleName: string, matrix: PermissionMatrix): Observable<void> {
    const allowedFeatures = this.matrixToFeatureKeys(matrix);
    const url = `${this.apiUrl}/${roleName}`;
    // Envoie la liste des chaînes de caractères 'ressource:action' au backend
    return this.http.put<void>(url, allowedFeatures );
  }
}
