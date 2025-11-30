import { Component, OnInit } from '@angular/core';
import { PermissionService } from '../../services/permission.service';
import { Permission } from '../../models/permission.model';
import { UtilisateurPermission } from '../../models/utilisateur-permission.model';
import { DEFAULT_ROLE_PERMISSIONS } from '../../shared/components/sidebar/sidebar.config';
import { UserRole } from '../../models/user.model';

@Component({
  selector: 'app-permission-management',
  templateUrl: './permission-management.component.html',
  styleUrls: ['./permission-management.component.css']
})
export class PermissionManagementComponent implements OnInit {

  utilisateurs: UtilisateurPermission[] = [];
  availablePermissions: Permission[] = [];
  selectedUser: UtilisateurPermission | null = null;
  selectedPermissions: number[] = [];
  showPermissionModal = false;

  constructor(private permissionService: PermissionService) {}

  ngOnInit(): void {
    this.loadUtilisateurs();
    this.loadAvailablePermissions();
  }

  private loadUtilisateurs(): void {
    this.permissionService.getUtilisateursAvecPermissions().subscribe({
      next: users => this.utilisateurs = users,
      error: err => console.error('Erreur chargement utilisateurs', err)
    });
  }

  private loadAvailablePermissions(): void {
    this.permissionService.getPermissions().subscribe({
      next: perms => this.availablePermissions = perms,
      error: err => console.error('Erreur chargement permissions', err)
    });
  }

  openPermissionModal(user: UtilisateurPermission): void {
    this.selectedUser = user;
    this.selectedPermissions = [...user.permissions]; // IDs
    this.showPermissionModal = true;
  }

  closePermissionModal(): void {
    this.selectedUser = null;
    this.selectedPermissions = [];
    this.showPermissionModal = false;
  }

  togglePermission(permissionId: number): void {
    const index = this.selectedPermissions.indexOf(permissionId);
    if (index > -1) {
      this.selectedPermissions.splice(index, 1);
    } else {
      this.selectedPermissions.push(permissionId);
    }
  }

  isPermissionSelected(permissionId: number): boolean {
    return this.selectedPermissions.includes(permissionId);
  }

  selectAllPermissions(): void {
    this.selectedPermissions = this.availablePermissions.map(p => p.id);
  }

  deselectAllPermissions(): void {
    this.selectedPermissions = [];
  }

 selectRoleDefaults(): void {
  if (!this.selectedUser) return;

  const role = this.selectedUser.role as UserRole;
  const defaults: string[] = DEFAULT_ROLE_PERMISSIONS[role] || [];

  // Convertir les strings en number
  this.selectedPermissions = defaults.map(id => Number(id));
}



  savePermissions(): void {
    if (!this.selectedUser) return;

    const updatedUser: UtilisateurPermission = {
      ...this.selectedUser,
      permissions: [...this.selectedPermissions]
    };

    this.permissionService.enregistrerPermissions([updatedUser]).subscribe({
      next: () => {
        this.loadUtilisateurs();
        this.closePermissionModal();
        alert('Permissions mises à jour avec succès !');
      },
      error: err => {
        console.error('Erreur sauvegarde permissions:', err);
        alert('Erreur lors de la sauvegarde des permissions');
      }
    });
  }

  resetToDefaultPermissions(user: UtilisateurPermission): void {
  if (!confirm(`Réinitialiser les permissions de ${user.nom} ${user.prenom} aux valeurs par défaut ?`)) return;

  const defaults = DEFAULT_ROLE_PERMISSIONS[user.role as UserRole] || [];

  // Convertir en number pour correspondre à UtilisateurPermission.permissions
  const numericDefaults = defaults.map(id => Number(id));

  const updatedUser: UtilisateurPermission = { ...user, permissions: numericDefaults };

  this.permissionService.enregistrerPermissions([updatedUser]).subscribe({
    next: () => {
      this.loadUtilisateurs();
      alert('Permissions réinitialisées aux valeurs par défaut !');
    },
    error: err => {
      console.error('Erreur réinitialisation permissions:', err);
      alert('Erreur lors de la réinitialisation');
    }
  });
}


  getPermissionCount(user: UtilisateurPermission): number {
    return user.permissions.length;
  }
}
