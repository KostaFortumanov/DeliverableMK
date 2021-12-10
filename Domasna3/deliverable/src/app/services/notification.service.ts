import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor() {}

  getNotifications() {
    return [
      {
        message: 'Driver 1 finished Job 1',
      },
      {
        message: 'Driver 2 finished Job 1',
      },
      {
        message: 'Driver 3 finished Job 1',
      },
      {
        message: 'Driver 1 finished Job 1',
      },
      {
        message: 'Driver 5 finished Job 1',
      },
      {
        message: 'Driver 1 finished Job 1',
      },
      {
        message: 'Driver 2 finished Job 1',
      },
    ];
  }
}
