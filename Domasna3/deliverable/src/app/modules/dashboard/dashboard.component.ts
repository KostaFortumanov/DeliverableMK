import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { DashboardService } from 'src/app/services/dashboard.service';

export interface Driver {
  name: string;
  position: number;
  deliveries: number;
  fuel: string;
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

  bigChart: Object[] = [];
  cards: Object[] = [];
  pieChart: Object[] = [];
  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<Driver>(
      this.dashboardService.table()
    );
    this.bigChart = this.dashboardService.bigChart();
    this.cards = this.dashboardService.cards();
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
