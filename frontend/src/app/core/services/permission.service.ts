import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Permission } from '../../models/permission.model';
import { UtilisateurPermission } from '../../models/utilisateur-permission.model';
import { environment } from '../../../environments/environment';
@Injectable({ providedIn: 'root' })
export class PermissionService {
  private apiUrl = `${environment.apiUrl}/permissions`;

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
