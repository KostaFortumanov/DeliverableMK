import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class SelectDriverService {
  constructor(private http: HttpClient) {}

  getDrivers(): Observable<any> {
    return this.http.get(API + '/user/selectDrivers');
  }
}
