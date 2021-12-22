import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  getDriverDashboard(month: number): Observable<any> {
    return this.http.get(API + '/dashboard/driver/' + month);
  }

  getManagerDashboard(month: number): Observable<any> {
    return this.http.get(API + '/dashboard/manager/' + month);
  }

  getDriverTotal(): Observable<any> {
    return this.http.get(API + '/dashboard/driverTotal');
  }

  getManagerTotal(): Observable<any> {
    return this.http.get(API + '/dashboard/managerTotal');
  }
}
