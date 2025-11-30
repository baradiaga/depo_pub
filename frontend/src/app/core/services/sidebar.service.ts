// Fichier : src/app/shared/services/sidebar.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';

import { Fonctionnalite, SousFonctionnalite } from '../../models/fonctionnalite.model';

@Injectable({
  providedIn: 'root'
})
export class SidebarService {

  private readonly featuresApiUrl = 'http://localhost:8080/api/fonctionnalites';
  private readonly myPermissionsApiUrl = 'http://localhost:8080/api/permissions/me';

  private allFeatures$ = new BehaviorSubject<Fonctionnalite[]>([]);
  private userPermissions$ = new BehaviorSubject<Set<string>>(new Set());

  public readonly visibleMenuItems$: Observable<Fonctionnalite[]>;

  constructor(private http: HttpClient) {
    this.loadInitialData();

    this.visibleMenuItems$ = combineLatest([
      this.allFeatures$,
      this.userPermissions$
    ]).pipe(
      map(([allFeatures, permissions]) => {
        console.log("%c--- SidebarService: Début du filtrage ---", "color: blue; font-weight: bold;");
        console.log("Fonctionnalités totales reçues:", allFeatures.length, allFeatures);
        console.log("Permissions utilisateur reçues:", permissions.size, permissions);

        const filteredMenu = this.filterMenu(allFeatures, permissions);

        console.log("%cMenu final après filtrage:", "color: green; font-weight: bold;", filteredMenu.length, filteredMenu);
        console.log("%c--- SidebarService: Fin du filtrage ---", "color: blue; font-weight: bold;");

        return filteredMenu;
      }),
      shareReplay(1)
    );
  }

  private loadInitialData(): void {
    // 1. Charger les fonctionnalités depuis l'API
    this.http.get<Fonctionnalite[]>(this.featuresApiUrl).pipe(
      tap(features => console.log('%cAPI Response /fonctionnalites:', 'color: orange;', features))
    ).subscribe(features => {
      this.allFeatures$.next(features);
    });

    // 2. Charger les permissions utilisateur
    this.http.get<string[]>(this.myPermissionsApiUrl).pipe(
      tap(permissionKeys => console.log('%cAPI Response /permissions/me:', 'color: purple;', permissionKeys))
    ).subscribe(permissionKeys => {
      this.userPermissions$.next(new Set(permissionKeys));
    });
  }

  private filterMenu(allFeatures: Fonctionnalite[], permissions: Set<string>): Fonctionnalite[] {
    const filteredMenu: Fonctionnalite[] = [];

    allFeatures.forEach(feature => {
      if (permissions.has(feature.featureKey)) {
        const visibleSubFeatures: SousFonctionnalite[] = feature.sousFonctionnalites
          .filter(sub => permissions.has(sub.featureKey))
          .map(sub => ({
            ...sub,
            queryParams: this.getQueryParamsFor(sub.featureKey) // ✅ Ajout automatique
          }));

        filteredMenu.push({
          ...feature,
          sousFonctionnalites: visibleSubFeatures
        });
      }
    });

    return filteredMenu;
  }

  private getQueryParamsFor(featureKey: string): { [key: string]: string } | undefined {
  switch (featureKey) {
    case 'parcours_recommandes_admin':
      return { type: 'RECOMMANDE' };
    case 'parcours_choisis_admin':
      return { type: 'CHOISI' };
    case 'parcours_mixtes_admin':
      return { type: 'MIXTE' };
    default:
      return undefined; // ✅ remplacer null par undefined
  }
}


  public refreshPermissions(): void {
    this.http.get<string[]>(this.myPermissionsApiUrl).subscribe(permissionKeys => {
      this.userPermissions$.next(new Set(permissionKeys));
    });
  }
}
