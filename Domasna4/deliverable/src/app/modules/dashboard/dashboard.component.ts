import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { finalize } from 'rxjs/operators';
import { Driver } from 'src/app/models/driver';
import { DashboardService } from 'src/app/services/dashboard.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  show = false;
  pageLoading = true;
  error = '';

  displayedColumns: string[] = ['name', 'distance', 'fuel', 'deliveries'];
  dataSource!: MatTableDataSource<Driver>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  role: string;
  data!: Object[];
  constructor(
    private dashboardService: DashboardService,
    private tokenStorageService: TokenStorageService
  ) {
    this.role = tokenStorageService.getUser().role;
  }

  ngOnInit(): void {
    if (this.role == 'DRIVER') {
      this.dashboardService
        .getDriverTotal()
        .pipe(
          finalize(() => {
            this.pageLoading = false;
            this.show = true;
            setTimeout(() => {
              this.dataSource.paginator = this.paginator;
            }, 100);
          })
        )
        .subscribe(
          (data) => {
            this.dataSource = new MatTableDataSource<Driver>(data);
          },
          (error) => {
            this.error = 'Server unavailable';
          }
        );
    } else {
      this.dashboardService
        .getManagerTotal()
        .pipe(
          finalize(() => {
            this.pageLoading = false;
            this.show = true;
            setTimeout(() => {
              this.dataSource.paginator = this.paginator;
            }, 100);
          })
        )
        .subscribe(
          (data) => {
            this.dataSource = new MatTableDataSource<Driver>(data);
            this.dataSource.paginator = this.paginator;
          },
          (error) => {
            this.error = 'Server unavailable';
          }
        );
    }
  }
}
