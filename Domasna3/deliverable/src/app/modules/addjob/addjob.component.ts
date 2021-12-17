import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import {
  startWith,
  map,
  debounceTime,
  distinctUntilChanged,
  finalize,
} from 'rxjs/operators';
import * as L from 'leaflet';
import { AddressService } from 'src/app/services/address.service';
import { JobService } from 'src/app/services/job.service';

export const _filter = (opt: string[], value: string): string[] => {
  const filterValue = value.toLowerCase();

  return opt.filter((item) => item.toLowerCase().includes(filterValue));
};

@Component({
  selector: 'app-addjob',
  templateUrl: './addjob.component.html',
  styleUrls: ['./addjob.component.scss'],
})
export class AddjobComponent implements OnInit {
  map!: L.Map;
  marker = L.marker(L.GeoJSON.coordsToLatLng([0, 0]));

  addJobLoading = false;
  addLocationLoading = false;
  addJobError = '';
  addJobSuccess = '';
  addLocationError = '';
  addLocationSuccess = '';

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
    this.marker.addTo(this.map);

    this.map.on('click', <LeafletMouseEvent>(e: { latlng: any }) => {
      this.marker.removeFrom(this.map);
      this.marker = L.marker(e.latlng);
      this.marker.addTo(this.map);
      this.addLocationLat = e.latlng.lat;
      this.addLocationLon = e.latlng.lng;
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

  addLocationLat: number = 0;
  addLocationLon: number = 0;

  constructor(
    private _formBuilder: FormBuilder,
    private addressService: AddressService,
    private jobService: JobService
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
    if (streetName) {
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
      );
    }
  }

  addJob(): void {
    let city = this.addJobForm.get('city')!.value;
    let street = this.addJobForm.get('street')!.value;
    let number = this.addJobForm.get('number')!.value;
    let description = this.addJobForm.get('description')!.value;

    this.addJobError = '';
    this.addJobSuccess = '';
    this.addJobLoading = true;
    this.jobService
      .addJob(city, street, number, description)
      .pipe(
        finalize(() => {
          this.addJobLoading = false;
        })
      )
      .subscribe(
        (data) => {
          this.addJobSuccess = data.message;
        },
        (error) => {
          if (error.status == '0') this.addJobError = 'Server unavailable';
          else this.addJobError = error.error.message;
        }
      );
  }

  addLocation(): void {
    let city = this.addLocationForm.get('addCity')!.value;
    let street = this.addLocationForm.get('addStreet')!.value;
    let number = this.addLocationForm.get('addNumber')!.value;

    this.addLocationSuccess = '';
    this.addLocationError = '';
    this.addLocationLoading = true;
    this.addressService
      .addLocation(
        city,
        street,
        number,
        this.addLocationLat,
        this.addLocationLon
      )
      .pipe(
        finalize(() => {
          this.addLocationLoading = false;
        })
      )
      .subscribe(
        (data) => {
          this.addLocationSuccess = data.message;
          this.getCities();
          this.initCityOptions();
          this.marker.removeFrom(this.map);
          this.addLocationLat = 0;
          this.addLocationLon = 0;
        },
        (error) => {
          if (error.status == '0') this.addLocationError = 'Server unavailable';
          else this.addLocationError = error.error.message;
        }
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
    this.tiles.addTo(this.map);
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
    this.streetOptions = this.addJobForm.get('street')!.valueChanges.pipe(
      startWith(''),
      debounceTime(400),
      distinctUntilChanged(),
      map((value) => this._filterStreetGroup(value))
    );
  }

  initNumberOptions() {
    this.numberOptions = this.addJobForm.get('number')!.valueChanges.pipe(
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
}
