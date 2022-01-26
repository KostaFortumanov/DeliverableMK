import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';
import { environment } from 'src/environments/environment';

export interface Notification {
  message: string;
}

const webSocketEndPoint = environment.wsEndPoint;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  @Output() toggleSideBarEvent: EventEmitter<any> = new EventEmitter();
  notifications: Notification[] = [];
  numNotifications: number = 0;

  stompClient: any;

  role!: string;

  constructor(
    private tokenStorageService: TokenStorageService,
    private notificationService: NotificationService,
    private router: Router
  ) {
    let user = tokenStorageService.getUser();
    if (user) {
      this.role = user.role;
    }

    if (this.role == 'MANAGER') {
      this.connect();
    }
  }

  connect() {
    let ws = new SockJS(webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    this.stompClient.debug = () => {}

    this.stompClient.connect(
      {},
      () => {
        this.stompClient.subscribe(
          '/user/managerJob/queue/messages',
          (message: any) => {
            message = JSON.parse(message.body);
            this.notifications.push(message);
          }
        );

      },
      this.errorCallBack
    );
  }

  dismissAll() {
    this.notificationService.dismissAll();
    this.notifications = [];
  }

  getNotifications() {
    return this.notifications;
  }

  getNumNotifications() {
    return this.notifications.length;
  }

  errorCallBack(error: string) {
    setTimeout(() => {
      this.connect();
    }, 5000);
  }

  ngOnInit(): void {
    this.notificationService.getNotifications().subscribe(
      (data) => {
        this.notifications = data;
        this.numNotifications = this.notifications.length;
      },
      (error) => {}
    );
  }

  toggleSideBar() {
    this.toggleSideBarEvent.emit();
    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 200);
  }

  signOut() {
    this.tokenStorageService.signOut();
    this.router.navigate(['/login']);
  }
}
