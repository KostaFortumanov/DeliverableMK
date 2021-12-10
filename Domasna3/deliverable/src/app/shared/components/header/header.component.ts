import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

export interface Notification {
  message: string;
}

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements OnInit {
  @Output() toggleSideBarEvent: EventEmitter<any> = new EventEmitter();
  notifications!: Notification[];
  numNotifications!: number;

  constructor(
    private tokenStorageService: TokenStorageService,
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.notifications = this.notificationService.getNotifications();
    this.numNotifications = this.notifications.length;
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
