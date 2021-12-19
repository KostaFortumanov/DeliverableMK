import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

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

  updateCurrentPath(
    lon: number,
    lat: number,
    destinationLon: number,
    destinationLat: number
  ): Observable<any> {
    return this.http.post(
      API + '/map/updateCurrentPath',
      {
        lon,
        lat,
        destinationLon,
        destinationLat
      },
      httpOptions
    );
  }
}
