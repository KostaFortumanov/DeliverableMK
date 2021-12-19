import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { DashboardService } from 'src/app/services/dashboard.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

export interface Driver {
  name: string;
  distance: number;
  fuel: number;
  numJobs: string;
}

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  displayedColumns: string[] = ['position', 'name', 'deliveries', 'fuel'];
  dataSource!: MatTableDataSource<Driver>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  cards: Object[] = [];
  role: string;
  data!: Object[];
  constructor(
    private dashboardService: DashboardService,
    private tokenStorageService: TokenStorageService
  ) {
    this.role = tokenStorageService.getUser().role;
  }

  ngOnInit(): void {

    if(this.role == 'DRIVER') {
    this.dashboardService.getDriverTotal().subscribe(
      (data) => {
        this.dataSource = new MatTableDataSource<Driver>(data);
      }
    )
    } else {
      this.dashboardService.getManagerTotal().subscribe(
        (data) => {
          this.dataSource = new MatTableDataSource<Driver>(data);
        }
      )
    }
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
