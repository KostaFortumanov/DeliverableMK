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

  topic: string =  "/user/" + 12 + "/queue/messages";
  stompClient: any;

  constructor(
    private tokenStorageService: TokenStorageService,
    private notificationService: NotificationService,
    private router: Router,
  ) {
    this.connect();
  }

  connect() {
    console.log("Initialize WebSocket Connection");
    let ws = new SockJS(webSocketEndPoint);
    this.stompClient = Stomp.over(ws);
    
    this.stompClient.connect({},  () => {
        this.stompClient.subscribe(this.topic,  (message: any) => {
          console.log(message)
          message = JSON.parse(message.body)
          this.notifications.push(message)
        });
        //_this.stompClient.reconnect_delay = 2000;
    }, this.errorCallBack);
  }

  dismissAll() {
    this.notificationService.dismissAll();
    this.notifications = []
  }

  getNotifications() {
    return this.notifications;
  }

  getNumNotifications() {
    return this.notifications.length;
  }

  onMessageReceived(message: any) {

    
    console.log("Message Recieved from Server :: " + message);
  }

  errorCallBack(error: string) {
    console.log('error')
    console.log("errorCallBack -> " + error)
    setTimeout(() => {
        this.connect();
    }, 5000);
  }

  ngOnInit(): void {
    this.notificationService.getNotifications()
      .subscribe(
        (data) => {
          this.notifications = data;
          this.numNotifications = this.notifications.length;
        }
      );

  }

  sendMessage() {
    this.stompClient.send( "/app/manager", {});
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
