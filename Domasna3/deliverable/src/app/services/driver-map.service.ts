import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class DriverMapService {
  constructor(private http: HttpClient) {}

  getPaths(): Observable<any> {
    return this.http.get(API + '/map/allPaths');
  }

  getCurrentJobsInfo(): Observable<any> {
    return this.http.get(API + '/map/currentJobs');
  }
}
