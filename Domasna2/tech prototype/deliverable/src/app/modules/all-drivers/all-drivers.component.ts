import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AllDriverDetailsService } from 'src/app/services/all-driver-details.service';

export interface Driver {
  name: string;
  position: number;
  email: string;
  phone: string;
}

@Component({
  selector: 'app-all-drivers',
  templateUrl: './all-drivers.component.html',
  styleUrls: ['./all-drivers.component.scss'],
})
export class AllDriversComponent implements OnInit {
  displayedColumns: string[] = [
    'position',
    'name',
    'email',
    'phone',
    'actions',
  ];
  dataSource!: MatTableDataSource<Driver>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private allDriverDetailsService: AllDriverDetailsService) {}

  ngOnInit(): void {
    this.dataSource = new MatTableDataSource<Driver>(
      this.allDriverDetailsService.getDrivers()
    );
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  edit(position: number) {
    console.log('edit: ' + position);
  }

  delete(position: number) {
    console.log('delete: ' + position);
  }
}
