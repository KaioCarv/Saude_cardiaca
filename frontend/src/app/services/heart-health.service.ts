import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  HeartHealthRecordRequest,
  HeartHealthRecord,
  HeartHealthRecordList,
  HeartHealthReport
} from '../models/heart-health.model';

@Injectable({
  providedIn: 'root'
})
export class HeartHealthService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createRecord(data: HeartHealthRecordRequest): Observable<HeartHealthRecord> {
    return this.http.post<HeartHealthRecord>(`${this.apiUrl}/heart-health-records`, data);
  }

  getRecords(startDate?: string, endDate?: string, limit?: number): Observable<HeartHealthRecordList> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (limit) params = params.set('limit', limit.toString());

    return this.http.get<HeartHealthRecordList>(`${this.apiUrl}/heart-health-records`, { params });
  }

  getReport(startDate?: string, endDate?: string): Observable<HeartHealthReport> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<HeartHealthReport>(`${this.apiUrl}/heart-health-reports`, { params });
  }
}
