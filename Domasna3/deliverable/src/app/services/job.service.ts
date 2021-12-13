import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root'
})
export class JobService {

  constructor(private http: HttpClient) { }

  addJob(city: string, street: string, number: string, description: string): Observable<any> {
    return this.http.post(
      API + '/jobs/addJob',
      {
        city,
        street,
        number,
        description
      },
      httpOptions
    )
  }

  assignJobs(driverIds: number[]): Observable<any> {
    return this.http.post(API + '/jobs/assignJobs', driverIds)
  }
}
