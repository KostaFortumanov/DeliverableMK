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
export class ConfigurationService {
  constructor(private http: HttpClient) {}

  getConfig(): Observable<any> {
    return this.http.get(API + '/config');
  }

  saveConfig(
    startTime: any,
    endTime: any,
    startLat: any,
    startLon: any
  ): Observable<any> {
    return this.http.post(
      API + '/config',
      {
        startTime,
        endTime,
        startLat,
        startLon,
      },
      httpOptions
    );
  }
}
