import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';
import { Notification } from 'src/app/models/notification';
import { WebSocketService } from 'src/app/services/web-socket.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  @Output() toggleSideBarEvent: EventEmitter<any> = new EventEmitter();
  notifications: Notification[] = [];
  numNotifications: number = 0;

  role!: string;

  constructor(
    private tokenStorageService: TokenStorageService,
    private notificationService: NotificationService,
    private webSocketService: WebSocketService,
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
    this.webSocketService.getStompClient().then((stompClient) => {
      stompClient.subscribe(
        '/user/managerJob/queue/messages',
        (message: any) => {
          message = JSON.parse(message.body);
          this.notifications.push(message);
        }
      );
   });
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

  ngOnInit(): void {
    if (this.role == 'MANAGER') {
      this.notificationService.getNotifications().subscribe(
        (data) => {
          this.notifications = data;
          this.numNotifications = this.notifications.length;
        },
        (error) => {}
      );
    }
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
