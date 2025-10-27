import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators'; // <-- Ajout de 'tap'

// Assurez-vous que le chemin vers votre modèle est correct
import { Fonctionnalite } from '../../models/fonctionnalite.model';

@Injectable({
  providedIn: 'root'
} )
export class SidebarService {
  
  private readonly featuresApiUrl = 'http://localhost:8080/api/fonctionnalites';
  private readonly myPermissionsApiUrl = 'http://localhost:8080/api/permissions/me';

  private allFeatures$ = new BehaviorSubject<Fonctionnalite[]>([] );
  private userPermissions$ = new BehaviorSubject<Set<string>>(new Set());

  public readonly visibleMenuItems$: Observable<Fonctionnalite[]>;

  constructor(private http: HttpClient ) {
    this.loadInitialData();

    this.visibleMenuItems$ = combineLatest([
      this.allFeatures$,
      this.userPermissions$
    ]).pipe(
      map(([allFeatures, permissions]) => {
        
        // --- LOG DE DÉBOGAGE N°2 : Affiche les données juste avant le filtrage ---
        console.log("%c--- SidebarService: Début du filtrage ---", "color: blue; font-weight: bold;");
        console.log("Fonctionnalités totales reçues:", allFeatures.length, allFeatures);
        console.log("Permissions utilisateur reçues:", permissions.size, permissions);
        // ----------------------------------------------------------------------

        const filteredMenu = this.filterMenu(allFeatures, permissions);
        
        // --- LOG DE DÉBOGAGE N°3 : Affiche le résultat final ---
        console.log("%cMenu final après filtrage:", "color: green; font-weight: bold;", filteredMenu.length, filteredMenu);
        console.log("%c--- SidebarService: Fin du filtrage ---", "color: blue; font-weight: bold;");
        // --------------------------------------------------------

        return filteredMenu;
      }),
      shareReplay(1) 
    );
  }

  private loadInitialData(): void {
    // 1. Récupérer la liste complète de toutes les fonctionnalités
    this.http.get<Fonctionnalite[]>(this.featuresApiUrl ).pipe(
      // --- LOG DE DÉBOGAGE N°1a : Affiche ce que l'API /fonctionnalites retourne ---
      tap(features => console.log('%cAPI Response /fonctionnalites:', 'color: orange;', features))
      // -----------------------------------------------------------------------------
    ).subscribe(features => {
      this.allFeatures$.next(features);
    });

    // 2. Récupérer les permissions spécifiques à l'utilisateur connecté
    this.http.get<string[]>(this.myPermissionsApiUrl ).pipe(
      // --- LOG DE DÉBOGAGE N°1b : Affiche ce que l'API /permissions/me retourne ---
      tap(permissionKeys => console.log('%cAPI Response /permissions/me:', 'color: purple;', permissionKeys))
      // ------------------------------------------------------------------------------
    ).subscribe(permissionKeys => {
      this.userPermissions$.next(new Set(permissionKeys));
    });
  }

  private filterMenu(allFeatures: Fonctionnalite[], permissions: Set<string>): Fonctionnalite[] {
    const filteredMenu: Fonctionnalite[] = [];
    allFeatures.forEach(feature => {
      if (permissions.has(feature.featureKey)) {
        const visibleSubFeatures = feature.sousFonctionnalites.filter(sub => 
          permissions.has(sub.featureKey)
        );
        filteredMenu.push({
          ...feature,
          sousFonctionnalites: visibleSubFeatures
        });
      }
    });
    return filteredMenu;
  }
  
  public refreshPermissions(): void {
    this.http.get<string[]>(this.myPermissionsApiUrl ).subscribe(permissionKeys => {
      this.userPermissions$.next(new Set(permissionKeys));
    });
  }
}
