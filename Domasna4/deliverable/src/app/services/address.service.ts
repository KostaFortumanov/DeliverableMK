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
export class AddressService {
  constructor(private http: HttpClient) {}

  getCities(): Observable<any> {
    return this.http.get(API + '/locations');
  }

  getStreets(cityName: string): Observable<any> {
    return this.http.get(API + '/locations/' + cityName);
  }

  getNumbers(cityName: string, streetName: string): Observable<any> {
    return this.http.get(API + '/locations/' + cityName + '/' + streetName);
  }

  addLocation(
    city: string,
    street: string,
    number: string,
    lat: number,
    lon: number
  ): Observable<any> {
    return this.http.post(
      API + '/locations/addLocation',
      {
        city,
        street,
        number,
        lat,
        lon,
      },
      httpOptions
    );
  }
}
