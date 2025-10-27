import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

// --- NOUVEAUX IMPORTS ---
import { SidebarService } from '../../../core/services/sidebar.service'; // Adaptez le chemin si nécessaire
import { Fonctionnalite } from '../../../models/fonctionnalite.model'; // Adaptez le chemin si nécessaire

// --- IMPORTS SUPPRIMÉS ---
// import { Subscription } from 'rxjs';
// import { AuthService } from '../../../services/auth.service';
// import { UserRole } from '../../../models/user.model';
// import { COMPLETE_SIDEBAR_CONFIG, MenuItem } from './sidebar.config'; // On n'utilise plus ce fichier ici

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  // La source de données pour le template est maintenant un unique observable.
  // Le pipe 'async' dans le HTML se chargera de la souscription.
  public menuItems$!: Observable<Fonctionnalite[]>;
  
  // Gardons la logique pour ouvrir/fermer les menus.
  public activeMenu: number | null = null;
  
  // Le constructeur n'a plus besoin de AuthService, seulement de SidebarService.
  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    // La seule chose à faire est d'assigner l'observable du service à notre variable.
    // Toute la logique de filtrage, de rôle, etc., est maintenant dans le service.
    this.menuItems$ = this.sidebarService.visibleMenuItems$;
  }

  /**
   * Gère l'ouverture et la fermeture des sous-menus en accordéon.
   * @param menuId L'ID de la fonctionnalité sur laquelle on a cliqué.
   */
  toggleSubMenu(menuId: number | undefined): void {
    // On vérifie que l'ID n'est pas undefined
    if (menuId === undefined) return;
    
    this.activeMenu = this.activeMenu === menuId ? null : menuId;
  }

  // ngOnDestroy n'est plus nécessaire car nous n'avons plus de souscription manuelle.
  // Le pipe 'async' dans le template gère le cycle de vie de la souscription automatiquement.
}
