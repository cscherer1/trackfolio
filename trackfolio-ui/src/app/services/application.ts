import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

// Keep this in sync with the Spring JSON (owner omitted on purpose)
export interface Application {
  id?: string;
  company: string;
  roleTitle: string;
  source: 'LINKEDIN' | 'COMPANY' | 'INDEED' | 'REFERRAL' | 'OTHER';
  status: 'APPLIED' | 'INTERVIEW' | 'OFFER' | 'REJECTED' | 'WITHDRAWN';
  appliedAt: string;  // ISO date string (e.g. '2025-08-28')
  location?: string;
  salaryText?: string;
  jobLink?: string;
  notes?: string;
  createdAt?: string;
  updatedAt?: string;
  deletedAt?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ApplicationService {
  private readonly baseUrl = '/api/applications'; // dev proxy forwards to Spring Boot

  constructor(private http: HttpClient) {}

  list(): Observable<Application[]> {
    return this.http
      .get<Application[]>(this.baseUrl)
      .pipe(catchError(this.handle));
  }

  create(payload: Partial<Application>): Observable<Application> {
    return this.http
      .post<Application>(this.baseUrl, payload)
      .pipe(catchError(this.handle));
  }

  find(id: string): Observable<Application> {
    return this.http.get<Application>(`${this.baseUrl}/${id}`).pipe(catchError(this.handle));
  }

  update(id: string, payload: Partial<Application>): Observable<Application> {
    return this.http.put<Application>(`${this.baseUrl}/${id}`, payload).pipe(catchError(this.handle));
  }


  delete(id: string) {
    return this.http
      .delete<void>(`${this.baseUrl}/${id}`)
      .pipe(catchError(this.handle));
  }

  private handle(err: HttpErrorResponse) {
    const msg =
      err.error?.message ??
      err.message ??
      `HTTP ${err.status}: ${err.statusText}`;
    return throwError(() => new Error(msg));
  }
}
