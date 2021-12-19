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

  getPreview(driverIds: number[]): Observable<any> {
    return this.http.post(API + '/jobs/preview', driverIds);
  }

  getUnassigned(): Observable<any> {
    return this.http.get(API + '/jobs/unassignedJobs');
  }

  getAssigned(): Observable<any> {
    return this.http.get(API + '/jobs/assignedJobs');
  }

  getCompleted(): Observable<any> {
    return this.http.get(API + '/jobs/completedJobs');
  }

  deleteJob(id: number): Observable<any> {
    return this.http.delete(API + '/jobs/delete/' + id);
  }

  getMyAssignedJobs(): Observable<any> {
    return this.http.get(API + '/jobs/myAssigned');
  }

  getMyCompletedJobs(): Observable<any> {
    return this.http.get(API + '/jobs/myCompleted');
  }
}
