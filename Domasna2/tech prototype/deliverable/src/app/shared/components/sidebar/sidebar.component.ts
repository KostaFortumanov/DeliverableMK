import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from 'src/app/services/token-storage.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  username!: string;
  email!: string;
  role!: string;

  constructor(private tokenStorageService: TokenStorageService) {
    let user = tokenStorageService.getUser();
    if (user) {
      this.username = user.username;
      this.email = user.email;
      this.role = 'ROLE_MANAGER';
    }
  }

  ngOnInit(): void {}
}
