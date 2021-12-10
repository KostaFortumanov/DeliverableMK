import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import {
  startWith,
  map,
  debounceTime,
  distinctUntilChanged,
} from 'rxjs/operators';
import * as L from 'leaflet';
import { AddressService } from 'src/app/services/address.service';

export const _filter = (opt: string[], value: string): string[] => {
  const filterValue = value.toLowerCase();

  return opt.filter((item) => item.toLowerCase().includes(filterValue));
};

export interface Address {
  id: number,
  number: number,
  lat: number,
  lon: number
}

@Component({
  selector: 'app-addjob',
  templateUrl: './addjob.component.html',
  styleUrls: ['./addjob.component.scss'],
})
export class AddjobComponent implements OnInit {
  map!: L.Map;
  addJobLoading = false;
  addLocationLoading = false;
  addJobError = '';
  addLocationError = '';

  cities: string[] = [];
  streets: string[] = [];
  numbers: string[] = [];

  cityOptions!: Observable<string[]>;
  streetOptions!: Observable<string[]>;
  numberOptions!: Observable<string[]>;

  private initMap(): void {
    this.map = L.map('addJobMap', {
      center: [41.9961, 21.4316],
      zoom: 13,
    });
  }

  private tiles = L.tileLayer(
    'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
    {
      maxZoom: 18,
      minZoom: 3,
      attribution:
        '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }
  );

  addJobForm: FormGroup = this._formBuilder.group({
    city: '',
    street: '',
    number: '',
    description: 'Име примач:\nОпис:\n',
  });

  addLocationForm: FormGroup = this._formBuilder.group({
    addCity: '',
    addStreet: '',
    addNumber: '',
  });

  constructor(
    private _formBuilder: FormBuilder,
    private addressService: AddressService
  ) {
    this.getCities();
  }

  getCities() {
    this.addressService.getCities().subscribe(
      (data) => {
        console.log(data);
        this.cities = data;
      },
      (error) => {
        console.log(error);
        this.streets = [];
        this.numbers = [];
      }
    );
  }

  getStreets(cityName: string) {
    this.addressService.getStreets(cityName).subscribe(
      (data) => {
        console.log(data);
        this.streets = data;
        this.addJobError = '';
      },
      (error) => {
        console.log(error);
        this.streets = [];
        this.numbers = [];
        this.addJobError = error.error.message;
      }
    );
  }

  getNumbers(cityName: string, streetName: string) {
    this.addressService.getNumbers(cityName, streetName).subscribe(
      (data) => {
        console.log(data);
        this.numbers = data;
        this.addJobError = '';
      },
      (error) => {
        console.log(error);
        this.numbers = [];
        this.addJobError = error.error.message;
      }
    )
  } 

  addJob(): void {
    console.log(
      this.addJobForm.get('city')!.value +
        ' ' +
        this.addJobForm.get('street')!.value +
        ' ' +
        this.addJobForm.get('number')!.value +
        ' ' +
        this.addJobForm.get('description')!.value
    );
  }

  addLocation(): void {
    console.log(
      this.addJobControls().cityGroup.value +
        ' ' +
        this.addJobControls().streetGroup.value +
        ' ' +
        this.addJobControls().numberGroup.value
    );
  }

  ngOnInit() {
    this.initCityOptions();

    this.addJobForm.get('city')?.valueChanges.subscribe((x) => {
      this.getStreets(x);
      this.getNumbers(x, this.addJobForm.get('street')?.value);
      this.initStreetOptions();
      this.initNumberOptions();
    });

    this.addJobForm.get('street')?.valueChanges.subscribe((x) => {
      this.getNumbers(this.addJobForm.get('city')?.value, x);
      this.initNumberOptions();
    });

    this.initMap();
    this.tiles.addTo(this.map)
  }

  initCityOptions() {
    this.cityOptions = this.addJobForm.get('city')!.valueChanges.pipe(
      startWith(''),
      debounceTime(400),
      distinctUntilChanged(),
      map((value) => this._filterCityGroup(value))
    );
  }

  initStreetOptions() {
    this.streetOptions = this.addJobForm
        .get('street')!
        .valueChanges.pipe(
          startWith(''),
          debounceTime(400),
          distinctUntilChanged(),
          map((value) => this._filterStreetGroup(value))
        );
  }

  initNumberOptions() {
    this.numberOptions = this.addJobForm
        .get('number')!
        .valueChanges.pipe(
          startWith(''),
          debounceTime(400),
          distinctUntilChanged(),
          map((value) => this._filterNumberGroup(value))
        );
  }

  private _filterCityGroup(value: string): string[] {
    const filterValue = this._normalizeValue(value);
    return this.cities.filter((city) =>
      this._normalizeValue(city).includes(filterValue)
    );
  }

  private _normalizeValue(value: string): string {
    return value.toLowerCase().replace(/\s/g, '');
  }

  private _filterStreetGroup(value: string): string[] {
    const filterValue = this._normalizeValue(value);
    return this.streets.filter((street) =>
      this._normalizeValue(street).includes(filterValue)
    );
  }

  private _filterNumberGroup(value: string): string[] {
    const filterValue = this._normalizeValue(value);
    return this.numbers.filter((number) =>
      this._normalizeValue(number).includes(filterValue)
    );
  }

  private addJobControls() {
    return this.addJobForm.controls;
  }

  private addLocationControls() {
    return this.addLocationForm.controls;
  }
}
