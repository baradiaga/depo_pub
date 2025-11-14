// Fichier : src/app/admin/permission-management/permission-management.component.ts (Version Corrigée)

import { Component, OnInit } from '@angular/core';
// ====================================================================
// === CORRECTION DU CHEMIN D'IMPORTATION                           ===
// ====================================================================
// On utilise un chemin relatif corrigé. Assurez-vous que ce chemin est bon pour votre projet.
// 'src/app/admin/...' -> '../core/...'
import { AuthService} from '../../services/auth.service'; 
import { COMPLETE_SIDEBAR_CONFIG, DEFAULT_ROLE_PERMISSIONS } from '../../shared/components/sidebar/sidebar.config';

// ... (le reste des interfaces est inchangé)
interface UserWithPermissions {
  id: string;
  email: string;
  
  permissions: string[];
  hasCustomPermissions: boolean;
  customPermissionInfo?: any;
}

interface PermissionGroup {
  label: string;
  featureKey: string;
  children: Array<{
    label: string;
    featureKey: string;
  }>;
}

@Component({
  selector: 'app-permission-management',
  templateUrl: './permission-management.component.html',
  styleUrls: ['./permission-management.component.css']
})
export class PermissionManagementComponent implements OnInit {
  users: UserWithPermissions[] = [];
  selectedUser: UserWithPermissions | null = null;
  availablePermissions: PermissionGroup[] = [];
  selectedPermissions: string[] = [];
  showPermissionModal = false;
  
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadAvailablePermissions();
  }

  private loadUsers(): void {
   
  }

  private loadAvailablePermissions(): void {
    this.availablePermissions = COMPLETE_SIDEBAR_CONFIG.map(item => ({
      label: item.label,
      featureKey: item.featureKey,
      children: item.children ? item.children.map(child => ({
        label: child.label,
        featureKey: child.featureKey
      })) : []
    }));
  }

  openPermissionModal(user: UserWithPermissions): void {
    this.selectedUser = user;
    this.selectedPermissions = [...user.permissions];
    this.showPermissionModal = true;
  }

  closePermissionModal(): void {
    this.showPermissionModal = false;
    this.selectedUser = null;
    this.selectedPermissions = [];
  }

  togglePermission(featureKey: string): void {
    const index = this.selectedPermissions.indexOf(featureKey);
    if (index > -1) {
      this.selectedPermissions.splice(index, 1);
    } else {
      this.selectedPermissions.push(featureKey);
    }
  }

  isPermissionSelected(featureKey: string): boolean {
    return this.selectedPermissions.includes(featureKey);
  }

  savePermissions(): void {
    if (!this.selectedUser) return;

    try {
     
        this.selectedUser.id,
        this.selectedPermissions,
       
      
      this.loadUsers();
      this.closePermissionModal();
      alert('Permissions mises à jour avec succès !');
    } catch (error) {
      console.error('Erreur lors de la sauvegarde des permissions:', error);
      alert('Erreur lors de la sauvegarde des permissions');
    }
  }

  resetToDefaultPermissions(user: UserWithPermissions): void {
    if (confirm(`Êtes-vous sûr de vouloir réinitialiser les permissions de ${user.email} aux valeurs par défaut de son rôle ?`)) {
      try {
        
        this.loadUsers();
        alert('Permissions réinitialisées aux valeurs par défaut !');
      } catch (error) {
        console.error('Erreur lors de la réinitialisation:', error);
        alert('Erreur lors de la réinitialisation des permissions');
      }
    }
  }

  selectAllPermissions(): void {
    this.selectedPermissions = [];
    this.availablePermissions.forEach(group => {
      this.selectedPermissions.push(group.featureKey);
      group.children.forEach(child => {
        this.selectedPermissions.push(child.featureKey);
      });
    });
  }

  deselectAllPermissions(): void {
    this.selectedPermissions = [];
  }

  selectRoleDefaults(): void {
    if (!this.selectedUser) return;
    // Pour l'erreur de type, on peut être plus explicite
   
  }

  getPermissionCount(user: UserWithPermissions): number {
    return user.permissions.length;
  }

 
}
