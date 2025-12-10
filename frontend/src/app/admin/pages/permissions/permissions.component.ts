// Fichier : src/app/admin/pages/permissions/permissions.component.ts

import { Component, OnInit } from '@angular/core';
import { PermissionsService, RolePermissionsMap, PermissionAction, PermissionMatrix } from '../services/permissions.service';
import { ToastrService } from 'ngx-toastr';
import { HttpErrorResponse } from '@angular/common/http';

type Action = PermissionAction;


@Component({
  selector: 'app-permissions',
  templateUrl: './permissions.component.html',
  styleUrls: ['./permissions.component.css']
} )
export class PermissionsComponent implements OnInit {
  roles: string[] = [];

  // Les ressources (tables) sont obtenues via le service
  resources: string[] = [];

  // Les actions CRUD sont maintenant explicites
  actions: Action[] = ['lister', 'lire', 'creer', 'modifier', 'supprimer'];
  
  // Map: { [roleName: string]: string[] (featureKeys autorisées) }
  rolePermissions: RolePermissionsMap = {};

  // La structure de permissions est maintenant une matrice complète
  // Map: { [role: string]: PermissionMatrix }
  permissions: { [role: string]: PermissionMatrix } = {};

  selectedRole: string = '';

  constructor(
    private permissionsService: PermissionsService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    // 1. Charger les rôles
    this.permissionsService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        // 2. Définir les ressources
        this.resources = this.permissionsService.getAvailableResources();
        // 3. Charger les permissions existantes
        this.permissionsService.getAllPermissionsByRole().subscribe({
          next: (rolePermissions) => {
            this.rolePermissions = rolePermissions;
            this.mapPermissionsToComponentStructure();
          },
          error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors du chargement des permissions.', 'Erreur API')
        });
      },
      error: (err: HttpErrorResponse) => this.toastr.error('Erreur lors du chargement des rôles.', 'Erreur API')
    });
  }

  mapPermissionsToComponentStructure(): void {
    this.permissions = {};
    this.roles.forEach(role => {
      const allowedFeatures = this.rolePermissions[role] || [];
      // Utiliser la méthode de conversion du service
      this.permissions[role] = this.permissionsService.featureKeysToMatrix(allowedFeatures);
    });
    // Sélectionner le premier rôle par défaut si la liste n'est pas vide
    if (this.roles.length > 0) {
      this.selectedRole = this.roles[0];
    }
  }

  togglePermission(role: string, resource: string, action: Action): void {
    if (role === 'ADMIN') {
      this.toastr.warning('Les permissions de l\'ADMIN ne peuvent pas être modifiées.', 'Action non autorisée');
      return;
    }
    
    // Basculer l'état de la permission dans la structure locale
    this.permissions[role][resource][action] = !this.permissions[role][resource][action];

    // La sauvegarde est maintenant manuelle via le bouton
    // this.savePermissions(role);
  }

  hasPermission(role: string, resource: string, action: Action): boolean {
    // Vérifie si la permission existe dans la matrice pour le rôle, la ressource et l'action
    return this.permissions[role] && this.permissions[role][resource] && this.permissions[role][resource][action];
  }

  isReadOnly(role: string): boolean {
    return role === 'ADMIN';
  }

  savePermissions(role: string): void {
    const matrixToSave = this.permissions[role];

    this.permissionsService.updatePermissionsForRole(role, matrixToSave).subscribe({
      next: () => {
        this.toastr.success(`Permissions pour le rôle ${role} mises à jour.`, 'Succès');
        // Mettre à jour la map locale pour la cohérence
        this.rolePermissions[role] = this.permissionsService.matrixToFeatureKeys(matrixToSave);
      },
      error: (err: HttpErrorResponse) => {
        this.toastr.error(`Erreur lors de la mise à jour des permissions pour ${role}.`, 'Erreur API');
        console.error(err);
        // Revenir à l'état précédent en cas d'erreur
        this.loadData();
      }
    });
  }
}
