import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Uefr } from '../models/uefr.model';

@Injectable({
  providedIn: 'root'
})
export class UefrService {
  private apiUrl = 'http://localhost:8080/uefrs';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Uefr[]> {
    return this.http.get<Uefr[]>(this.apiUrl);
  }

  getById(id: number): Observable<Uefr> {
    return this.http.get<Uefr>(`${this.apiUrl}/${id}`);
  }

  create(uefr: Uefr): Observable<Uefr> {
    return this.http.post<Uefr>(this.apiUrl, uefr);
  }

  update(id: number, uefr: Uefr): Observable<Uefr> {
    return this.http.put<Uefr>(`${this.apiUrl}/${id}`, uefr);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
