// Fichier : src/app/shared/components/sidebar/sidebar.component.ts (Version finale et corrigée)

import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';

import { SidebarService } from '../../../core/services/sidebar.service';
import { Fonctionnalite } from '../../../models/fonctionnalite.model';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {

  public menuItems$!: Observable<Fonctionnalite[]>;
  public activeMenu: number | null = null;
  
  constructor(private sidebarService: SidebarService) {}

  ngOnInit(): void {
    this.menuItems$ = this.sidebarService.visibleMenuItems$;
  }

  toggleSubMenu(menuId: number | undefined): void {
    if (menuId === undefined) return;
    this.activeMenu = this.activeMenu === menuId ? null : menuId;
  }

  /**
   * Retourne un objet de queryParams en fonction du feature_key du lien.
   * @param featureKey La clé unique de la sous-fonctionnalité.
   */
  getQueryParamsFor(featureKey: string | undefined): { [key: string]: string } | null {
    if (!featureKey) {
      return null;
    }

    // ====================================================================
    // === CORRECTION APPLIQUÉE ICI                                     ===
    // === On utilise maintenant les clés exactes de votre base de données. ===
    // ====================================================================
    switch (featureKey) {
      case 'parcours_recommandes_etudiant':
        return { type: 'recommandes' };
      case 'parcours_choisis_etudiant':
        return { type: 'choisis' };
      case 'parcours_mixtes_etudiant':
        return { type: 'mixtes' };
      default:
        return null;
    }
  }
}
