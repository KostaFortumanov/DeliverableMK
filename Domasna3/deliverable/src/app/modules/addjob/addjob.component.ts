import { AfterViewInit, Component } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { startWith, map } from 'rxjs/operators';
import * as L from 'leaflet';
import { AddressService } from 'src/app/services/address.service';

export interface CityGroup {
  letter: string;
  names: string[];
}

export interface StreetGroup {
  letter: string;
  names: string[];
}

export interface NumberGroup {
  numbers: string[];
}

export const _filter = (opt: string[], value: string): string[] => {
  const filterValue = value.toLowerCase();

  return opt.filter((item) => item.toLowerCase().includes(filterValue));
};

@Component({
  selector: 'app-addjob',
  templateUrl: './addjob.component.html',
  styleUrls: ['./addjob.component.scss'],
})
export class AddjobComponent implements AfterViewInit {
  map!: L.Map;
  addJobLoading = false;
  addLocationLoading = false;
  addJobError = '';
  addLocationError = '';

  cityGroups: CityGroup[];
  streetGroups: StreetGroup[];
  numberGroups: NumberGroup[];

  cityGroupOptions!: Observable<CityGroup[]>;
  streetGroupOptions!: Observable<StreetGroup[]>;
  numberGroupOptions!: Observable<NumberGroup[]>;

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
    cityGroup: '',
    streetGroup: '',
    numberGroup: '',
    description: 'Име примач:\nОпис:\n',
  });

  addLocationForm: FormGroup = this._formBuilder.group({
    city: '',
    street: '',
    number: '',
  });

  constructor(
    private _formBuilder: FormBuilder,
    private addressService: AddressService
  ) {
    this.cityGroups = addressService.getCities();
    this.streetGroups = addressService.getStreets();
    this.numberGroups = addressService.getNumbers();
  }

  addJob(): void {
    console.log(
      this.addJobControls().cityGroup.value +
        ' ' +
        this.addJobControls().streetGroup.value +
        ' ' +
        this.addJobControls().numberGroup.value +
        ' ' +
        this.addJobControls().description.value
    );
  }

  addLocation(): void {
    console.log(
      this.addLocationControls().city.value +
        ' ' +
        this.addLocationControls().street.value +
        ' ' +
        this.addLocationControls().number.value
    );
  }

  ngAfterViewInit() {
    this.streetGroupOptions = this.addJobForm
      .get('streetGroup')!
      .valueChanges.pipe(
        startWith(''),
        map((value) => this._filterStreetGroup(value))
      );
    this.cityGroupOptions = this.addJobForm.get('cityGroup')!.valueChanges.pipe(
      startWith(''),
      map((value) => this._filterCityGroup(value))
    );
    this.numberGroupOptions = this.addJobForm
      .get('numberGroup')!
      .valueChanges.pipe(
        startWith(''),
        map((value) => this._filterNumberGroup(value))
      );
    this.initMap();
    this.tiles.addTo(this.map);
  }

  private _filterCityGroup(value: string): CityGroup[] {
    if (value) {
      return this.cityGroups
        .map((group) => ({
          letter: group.letter,
          names: _filter(group.names, value),
        }))
        .filter((group) => group.names.length > 0);
    }

    return this.cityGroups;
  }

  private _filterStreetGroup(value: string): StreetGroup[] {
    if (value) {
      return this.streetGroups
        .map((group) => ({
          letter: group.letter,
          names: _filter(group.names, value),
        }))
        .filter((group) => group.names.length > 0);
    }
    return this.streetGroups;
  }

  private _filterNumberGroup(value: string): NumberGroup[] {
    if (value) {
      return this.numberGroups.map((group) => ({
        numbers: _filter(group.numbers, value),
      }));
    }

    return this.numberGroups;
  }

  private addJobControls() {
    return this.addJobForm.controls;
  }

  private addLocationControls() {
    return this.addLocationForm.controls;
  }
}
