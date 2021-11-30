import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AddressService {
  constructor() {}

  getCities() {
    return [
      {
        letter: 'S',
        names: ['Skopje'],
      },
    ];
  }

  getStreets() {
    return [
      {
        letter: 'A',
        names: ['Arhitekt Nikola Dobrovikj'],
      },
      {
        letter: 'B',
        names: ['Bahar Mois', 'Brsjacka Buna'],
      },
      {
        letter: 'J',
        names: ['Jane Sandanski'],
      },
      {
        letter: 'K',
        names: ['Kumanovska'],
      },
      {
        letter: 'N',
        names: ['Naum Naumovski Borce', 'Nikola Kirov - Majski'],
      },
      {
        letter: 'O',
        names: ['Oktomvriska'],
      },
      {
        letter: 'P',
        names: ['Petar Pop Arsov', 'Petar Cajkovski'],
      },
    ];
  }

  getNumbers() {
    return [
      {
        numbers: ['11', '12', '23', '54'],
      },
    ];
  }
}
