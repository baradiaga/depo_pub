import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { FonctionnaliteAdminService } from '../services/fonctionnalite-admin.service';
import { Fonctionnalite, PermissionsMap } from '../../../models/fonctionnalite.model';

// Interface pour la structure de données adaptée au template
interface FeatureGroup {
  module: string; // Correspond à la featureKey de la fonctionnalité principale
  subFeatures: string[]; // Correspond aux featureKey des sous-fonctionnalités
}

@Component({
  selector: 'app-feature-assignment',
  templateUrl: './feature-assignment.component.html',
  styleUrls: ['./feature-assignment.component.css'] // À créer si nécessaire
})
export class FeatureAssignmentComponent implements OnInit, OnDestroy {

  // --- État ---
  selectedRole: string = '';
  private subs = new Subscription();

  // Données brutes du service
  allFonctionnalites: Fonctionnalite[] = [];
  permissionsMap: PermissionsMap = {};
  roles: string[] = ['ADMIN', 'ETUDIANT', 'ENSEIGNANT', 'TUTEUR', 'TECHNOPEDAGOGUE', 'RESPONSABLE_FORMATION'];

  // Données pour le template
  allFeatures: FeatureGroup[] = [];
  selectedRolePermissions = new Set<string>();
  visibleModules = new Set<string>(); // Pour gérer l'affichage des sous-fonctionnalités

  constructor(private adminService: FonctionnaliteAdminService) {}

  ngOnInit(): void {
    // Le service doit déjà avoir chargé les données, mais on s'abonne
    this.subs.add(this.adminService.fonctionnalites$.subscribe(data => {
      this.allFonctionnalites = data;
      this.transformFonctionnalitesToFeatures();
    }));

    this.subs.add(this.adminService.permissions$.subscribe(data => {
      this.permissionsMap = data;
      // Si un rôle est déjà sélectionné, on rafraîchit les permissions
      if (this.selectedRole) {
        this.loadPermissionsForRole(this.selectedRole);
      }
    }));
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  /**
   * Transforme la liste plate de Fonctionnalite en FeatureGroup pour le template.
   */
  private transformFonctionnalitesToFeatures(): void {
    this.allFeatures = this.allFonctionnalites.map(f => ({
      module: f.featureKey, // Utiliser la featureKey comme nom de module
      subFeatures: f.sousFonctionnalites.map(sf => sf.featureKey)
    }));
  }

  /**
   * Charge les permissions pour le rôle sélectionné.
   */
  loadPermissionsForRole(roleName: string): void {
    this.selectedRolePermissions = new Set(this.permissionsMap[roleName] || []);
    // Réinitialiser l'état d'affichage des sous-fonctionnalités
    this.visibleModules.clear();
    this.allFeatures.forEach(group => {
      // On vérifie si la permission principale est cochée
      if (this.selectedRolePermissions.has(group.module)) {
        this.visibleModules.add(group.module);
      }
    });
  }

  /**
   * Gère le changement de rôle via ngModel.
   */
  onRoleChange(): void {
    if (this.selectedRole) {
      this.loadPermissionsForRole(this.selectedRole);
    } else {
      this.selectedRolePermissions.clear();
      this.visibleModules.clear();
    }
  }

  /**
   * Vérifie si une fonctionnalité (ou sous-fonctionnalité) est assignée au rôle.
   */
  isFeatureAssigned(featureKey: string): boolean {
    return this.selectedRolePermissions.has(featureKey);
  }

  /**
   * Bascule l'état d'une fonctionnalité (ou sous-fonctionnalité).
   */
  toggleFeatureAssignment(featureKey: string): void {
    if (this.selectedRolePermissions.has(featureKey)) {
      this.selectedRolePermissions.delete(featureKey);
    } else {
      this.selectedRolePermissions.add(featureKey);
    }
  }

  /**
   * Gère le basculement de la fonctionnalité principale (module).
   * Coche/décoche la fonctionnalité principale et toutes ses sous-fonctionnalités.
   */
  onMainFeatureToggle(event: Event, moduleKey: string): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    const featureGroup = this.allFeatures.find(f => f.module === moduleKey);

    if (!featureGroup) return;

    // 1. Basculer l'état de la fonctionnalité principale
    if (isChecked) {
      this.selectedRolePermissions.add(moduleKey);
      this.visibleModules.add(moduleKey);
    } else {
      this.selectedRolePermissions.delete(moduleKey);
      this.visibleModules.delete(moduleKey);
    }

    // 2. Basculer l'état de toutes les sous-fonctionnalités
    featureGroup.subFeatures.forEach(subKey => {
      if (isChecked) {
        this.selectedRolePermissions.add(subKey);
      } else {
        this.selectedRolePermissions.delete(subKey);
      }
    });
  }

  /**
   * Enregistre les permissions mises à jour.
   */
  enregistrer(): void {
    if (!this.selectedRole) {
      alert('Veuillez sélectionner un rôle avant d\'enregistrer.');
      return;
    }
    const permissionsToSave = Array.from(this.selectedRolePermissions);
    this.subs.add(
      this.adminService.savePermissionsForRole(this.selectedRole, permissionsToSave).subscribe(() => {
        alert(`Permissions pour le rôle ${this.selectedRole} enregistrées avec succès !`);
      })
    );
  }
}
