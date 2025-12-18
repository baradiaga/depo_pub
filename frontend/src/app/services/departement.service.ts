import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Departement } from '../models/departement.model';

export interface DepartementSearchParams {
  search?: string;
  uefrId?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface DepartementSearchResult {
  content: Departement[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class DepartementService {
  
  private apiUrl = 'http://localhost:8080/api/departements';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Departement[]> {
    return this.http.get<Departement[]>(this.apiUrl);
  }

  search(params: DepartementSearchParams): Observable<DepartementSearchResult> {
    let httpParams = new HttpParams();
    
    if (params.search) {
      httpParams = httpParams.set('search', params.search);
    }
    
    if (params.uefrId && params.uefrId > 0) {
      httpParams = httpParams.set('uefrId', params.uefrId.toString());
    }
    
    if (params.page) {
      httpParams = httpParams.set('page', (params.page - 1).toString());
    }
    
    if (params.size) {
      httpParams = httpParams.set('size', params.size.toString());
    }
    
    if (params.sortBy) {
      const sort = `${params.sortBy},${params.sortOrder || 'asc'}`;
      httpParams = httpParams.set('sort', sort);
    }
    
    return this.http.get<DepartementSearchResult>(`${this.apiUrl}/search`, { 
      params: httpParams 
    });
  }

  getById(id: number): Observable<Departement> {
    return this.http.get<Departement>(`${this.apiUrl}/${id}`);
  }

  create(departement: Departement): Observable<Departement> {
    return this.http.post<Departement>(this.apiUrl, departement);
  }

  update(id: number, departement: Departement): Observable<Departement> {
    return this.http.put<Departement>(`${this.apiUrl}/${id}`, departement);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}