import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

const AUTH_API = environment.apiUrl;

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
};

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  constructor(private http: HttpClient) {}

  login(username: string, password: string): Observable<any> {
    return this.http.post(
      AUTH_API + '/auth/login',
      {
        username,
        password,
      },
      httpOptions
    );
  }

  register(
    firstName: string,
    lastName: string,
    email: string,
    phoneNumber: string,
    userRole: string
  ): Observable<any> {
    return this.http.post(
      AUTH_API + '/auth/register',
      {
        firstName,
        lastName,
        email,
        phoneNumber,
        userRole,
      },
      httpOptions
    );
  }

  newAccount(
    token: string,
    password: string,
    repeatPassword: string
  ): Observable<any> {
    return this.http.post(
      AUTH_API + '/auth/newAccount',
      {
        token,
        password,
        repeatPassword,
      },
      httpOptions
    );
  }
}
