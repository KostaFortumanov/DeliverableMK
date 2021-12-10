import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor() {}

  bigChart() {
    return [
      {
        name: 'Километри',
        data: [502, 635, 809, 947, 1402, 3634, 5268],
      },
      {
        name: 'Гориво',
        data: [18, 31, 54, 156, 339, 818, 1201],
      },
    ];
  }

  cards() {
    return [71, 78, 39, 66, 43];
  }

  table() {
    return [
      {
        position: 1,
        name: 'Костадин Фортуманов',
        deliveries: 70,
        fuel: '192L',
      },
      { position: 2, name: 'Никола Ценевски', deliveries: 80, fuel: '180L' },
      { position: 3, name: 'Станко Станчевски', deliveries: 67, fuel: '167L' },
      { position: 4, name: 'Петар Петровски', deliveries: 43, fuel: '143L' },
      { position: 5, name: 'Стефан Стефанов', deliveries: 23, fuel: '123L' },
    ];
  }
}
