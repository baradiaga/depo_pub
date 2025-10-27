import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Permission } from '../models/permission.model';
import { UtilisateurPermission } from '../models/utilisateur-permission.model';

@Injectable({ providedIn: 'root' })
export class PermissionService {
  private apiUrl = 'http://localhost:8080/api/permissions';

  constructor(private http: HttpClient) {}

  getPermissions(): Observable<Permission[]> {
    return this.http.get<Permission[]>(`${this.apiUrl}/all`);
  }

  getUtilisateursAvecPermissions(): Observable<UtilisateurPermission[]> {
    return this.http.get<UtilisateurPermission[]>(`${this.apiUrl}/utilisateurs`);
  }

  enregistrerPermissions(data: UtilisateurPermission[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/save`, data);
  }
}
