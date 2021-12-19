import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const API = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private http: HttpClient) {}

  getNotifications(): Observable<any> {
    return this.http.get(API + '/jobs/notifications');
  }

  dismissAll() {
    this.http.delete(API + '/jobs/deleteAllNotifications').subscribe();
  }
}
