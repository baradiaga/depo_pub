import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { FonctionnaliteAdminService } from '../services/fonctionnalite-admin.service';
import { Fonctionnalite, PermissionsMap } from '../../../models/fonctionnalite.model'; // Adaptez si votre dossier 'models' est ailleurs

@Component({
  selector: 'app-fonctionnalites',
  templateUrl: './fonctionnalites.component.html',
  styleUrls: ['./fonctionnalites.component.css']
})
export class FonctionnalitesComponent implements OnInit, OnDestroy {

  // --- État général ---
  currentView: 'gestion' | 'permissions' = 'gestion';
  private subs = new Subscription();

  // --- Données pour les deux vues ---
  allFonctionnalites: Fonctionnalite[] = [];
  permissionsMap: PermissionsMap = {};
  roles: string[] = ['ADMIN', 'ETUDIANT', 'ENSEIGNANT', 'TUTEUR', 'TECHNOPEDAGOGUE', 'RESPONSABLE_FORMATION'];

  // --- État pour le formulaire de GESTION (CRUD) ---
  isFormVisible = false;
  editingFonctionnalite: Fonctionnalite | null = null;
  
  // --- État pour la vue PERMISSIONS ---
  selectedRole: string | null = null;
  selectedRolePermissions = new Set<string>();

  constructor(private adminService: FonctionnaliteAdminService) {}

  ngOnInit(): void {
    this.adminService.loadAllAdminData();

    this.subs.add(this.adminService.fonctionnalites$.subscribe(data => this.allFonctionnalites = data));
    this.subs.add(this.adminService.permissions$.subscribe(data => {
      this.permissionsMap = data;
      if (this.selectedRole) this.selectRoleForPermissions(this.selectedRole); // Rafraîchir
    }));
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  // --- Méthodes pour le mode GESTION (CRUD) ---

  startNewFonctionnalite(): void {
    this.editingFonctionnalite = { nom: '', featureKey: '', icon: '', sousFonctionnalites: [] };
    this.isFormVisible = true;
  }

  startEditFonctionnalite(fonctionnalite: Fonctionnalite): void {
    // Crée une copie profonde pour éviter de modifier l'original avant la sauvegarde
    this.editingFonctionnalite = JSON.parse(JSON.stringify(fonctionnalite));
    this.isFormVisible = true;
  }

  saveFonctionnalite(): void {
    if (!this.editingFonctionnalite) return;

    if (this.editingFonctionnalite.id) { // Mise à jour
      this.subs.add(this.adminService.updateFonctionnalite(this.editingFonctionnalite.id, this.editingFonctionnalite).subscribe(() => this.closeForm()));
    } else { // Création
      const { id, ...newFonctionnalite } = this.editingFonctionnalite;
      this.subs.add(this.adminService.createFonctionnalite(newFonctionnalite).subscribe(() => this.closeForm()));
    }
  }

  deleteFonctionnalite(id: number | undefined): void {
    if (id === undefined) return;
    if (confirm('Êtes-vous sûr de vouloir supprimer cette fonctionnalité et tous ses sous-menus ?')) {
      this.subs.add(this.adminService.deleteFonctionnalite(id).subscribe());
    }
  }

  addSubFeature(): void {
    if (!this.editingFonctionnalite) return;
    this.editingFonctionnalite.sousFonctionnalites.push({ label: '', featureKey: '', route: '' });
  }

  removeSubFeature(index: number): void {
    if (!this.editingFonctionnalite) return;
    this.editingFonctionnalite.sousFonctionnalites.splice(index, 1);
  }

  closeForm(): void {
    this.isFormVisible = false;
    this.editingFonctionnalite = null;
  }

  // --- Méthodes pour le mode PERMISSIONS ---

  selectRoleForPermissions(roleName: string): void {
    this.selectedRole = roleName;
    this.selectedRolePermissions = new Set(this.permissionsMap[roleName] || []);
  }

  isFeatureChecked(featureKey: string): boolean {
    return this.selectedRolePermissions.has(featureKey);
  }

  toggleFeaturePermission(featureKey: string): void {
    if (this.selectedRolePermissions.has(featureKey)) {
      this.selectedRolePermissions.delete(featureKey);
    } else {
      this.selectedRolePermissions.add(featureKey);
    }
  }

  savePermissions(): void {
    if (!this.selectedRole) return;
    const permissionsToSave = Array.from(this.selectedRolePermissions);
    this.subs.add(
      this.adminService.savePermissionsForRole(this.selectedRole, permissionsToSave).subscribe(() => {
        alert(`Permissions pour le rôle ${this.selectedRole} enregistrées !`);
      })
    );
  }
   onRoleChange(event: Event): void {
    // 1. On s'assure que la cible de l'événement est bien un élément HTML de type <select>
    const selectElement = event.target as HTMLSelectElement;

    // 2. On récupère la valeur de l'élément sélectionné
    const roleName = selectElement.value;

    // 3. On appelle votre méthode de logique métier existante avec la valeur propre
    this.selectRoleForPermissions(roleName);
  }
}
