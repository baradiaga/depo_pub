// Fichier : src/app/shared/components/sidebar/sidebar.component.ts

import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

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

  constructor(
    private sidebarService: SidebarService,
    private router: Router          // ✅ AJOUT OBLIGATOIRE
  ) {}

  ngOnInit(): void {
    this.menuItems$ = this.sidebarService.visibleMenuItems$;
  }

  toggleSubMenu(menuId: number | undefined): void {
    if (menuId === undefined) return;
    this.activeMenu = this.activeMenu === menuId ? null : menuId;
  }

  /**
   * Navigation centralisée pour TOUS les sous menus.
   * Appelée depuis le HTML.
   */
  navigateTo(child: any): void {
    if (!child) return;

    const queryParams = this.getQueryParamsFor(child.featureKey);

    this.router.navigate([child.route], {
      queryParams: queryParams ?? undefined
    });
  }

  /**
   * Retourne les bons query params en fonction du featureKey
   */
  getQueryParamsFor(featureKey: string | undefined): { [key: string]: string } | null {

    if (!featureKey) return null;

    switch (featureKey) {

      // === ETUDIANT ===
      case 'parcours_recommandes_etudiant':
        return { type: 'recommandes' };

      case 'parcours_choisis_etudiant':
        return { type: 'choisis' };

      case 'parcours_mixtes_etudiant':
        return { type: 'mixtes' };

      // === ADMIN (tu utilisais déjà RECOMMANDE / CHOISI / MIXTE) ===
      case 'parcours_recommandes_admin':
        return { type: 'RECOMMANDE' };

      case 'parcours_choisis_admin':
        return { type: 'CHOISI' };

      case 'parcours_mixtes_admin':
        return { type: 'MIXTE' };

      default:
        return null;
    }
  }

}
