import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, forkJoin } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Fonctionnalite, SousFonctionnalite, PermissionsMap } from '../../../models/fonctionnalite.model';

@Injectable({
  providedIn: 'root'
} )
export class FonctionnaliteAdminService {

  private readonly featureApiUrl = 'http://localhost:8080/api/fonctionnalites';
  private readonly permissionApiUrl = 'http://localhost:8080/api/permissions';

  // Sujet pour la liste complète des fonctionnalités (pour le CRUD et l'affichage )
  private fonctionnalitesSubject = new BehaviorSubject<Fonctionnalite[]>([]);
  public fonctionnalites$ = this.fonctionnalitesSubject.asObservable();

  // Sujet pour la map des permissions par rôle
  private permissionsSubject = new BehaviorSubject<PermissionsMap>({});
  public permissions$ = this.permissionsSubject.asObservable();

  constructor(private http: HttpClient ) {}

  // --- Initialisation ---
  loadAllAdminData(): void {
    forkJoin({
      fonctionnalites: this.http.get<Fonctionnalite[]>(this.featureApiUrl ),
      permissions: this.http.get<PermissionsMap>(this.permissionApiUrl )
    }).subscribe(data => {
      this.fonctionnalitesSubject.next(data.fonctionnalites);
      this.permissionsSubject.next(data.permissions);
    });
  }

  // --- API pour le CRUD des Fonctionnalités ---

  createFonctionnalite(fonctionnalite: Omit<Fonctionnalite, 'id'>): Observable<Fonctionnalite> {
    return this.http.post<Fonctionnalite>(this.featureApiUrl, fonctionnalite ).pipe(
      tap(newFeature => {
        const current = this.fonctionnalitesSubject.getValue();
        this.fonctionnalitesSubject.next([...current, newFeature]);
      })
    );
  }

  updateFonctionnalite(id: number, fonctionnalite: Fonctionnalite): Observable<Fonctionnalite> {
    return this.http.put<Fonctionnalite>(`${this.featureApiUrl}/${id}`, fonctionnalite ).pipe(
      tap(updatedFeature => {
        const current = this.fonctionnalitesSubject.getValue();
        const index = current.findIndex(f => f.id === id);
        if (index > -1) {
          current[index] = updatedFeature;
          this.fonctionnalitesSubject.next([...current]);
        }
      })
    );
  }

  deleteFonctionnalite(id: number): Observable<void> {
    return this.http.delete<void>(`${this.featureApiUrl}/${id}` ).pipe(
      tap(() => {
        const current = this.fonctionnalitesSubject.getValue();
        this.fonctionnalitesSubject.next(current.filter(f => f.id !== id));
      })
    );
  }

  // --- API pour les Permissions ---

  savePermissionsForRole(roleName: string, featureKeys: string[]): Observable<void> {
    return this.http.put<void>(`${this.permissionApiUrl}/${roleName}`, featureKeys ).pipe(
      tap(() => {
        const current = this.permissionsSubject.getValue();
        current[roleName] = featureKeys;
        this.permissionsSubject.next({ ...current });
      })
    );
  }
}
