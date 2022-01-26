import { Component, OnInit, Input } from '@angular/core';
import * as Highcharts from 'highcharts';
import HC_exporting from 'highcharts/modules/exporting';
import { DashboardService } from 'src/app/services/dashboard.service';
import { TokenStorageService } from 'src/app/services/token-storage.service';

export interface Distance {
  name: string;
  data: any;
}

export interface Fuel {
  name: string;
  data: any;
}

export interface NumJobs {
  name: string;
  data: any;
}

@Component({
  selector: 'app-widget-area',
  templateUrl: './area.component.html',
  styleUrls: ['./area.component.scss'],
})
export class AreaComponent implements OnInit {
  months = [
    { value: 1, name: 'January' },
    { value: 2, name: 'February' },
    { value: 3, name: 'March' },
    { value: 4, name: 'April' },
    { value: 5, name: 'May' },
    { value: 6, name: 'June' },
    { value: 7, name: 'July' },
    { value: 8, name: 'August' },
    { value: 9, name: 'September' },
    { value: 10, name: 'October' },
    { value: 11, name: 'November' },
    { value: 12, name: 'December' },
  ];

  chartOptions!: any;
  selected: any;

  distance: Distance = { name: 'Kilometers', data: [] };
  fuel: Fuel = { name: 'Fuel', data: [] };
  numJobs: NumJobs = { name: 'Jobs', data: [] };
  Highcharts = Highcharts;
  role: string;
  data!: Object[];
  constructor(
    private dashboardService: DashboardService,
    private tokenStorageService: TokenStorageService
  ) {
    this.role = tokenStorageService.getUser().role;
  }

  ngOnInit(): void {
    let month = new Date().getMonth() + 1;
    this.selected = month;

    if (this.role === 'DRIVER') {
      this.dashboardService.getDriverDashboard(month).subscribe((data) => {
        this.init(data);
      });
    } else {
      this.dashboardService.getManagerDashboard(month).subscribe((data) => {
        this.init(data);
      });
    }

    HC_exporting(Highcharts);

    setTimeout(() => {
      window.dispatchEvent(new Event('resize'));
    }, 300);
  }

  init(data: any) {
    for (let day of data) {
      this.distance.data.push(Math.round(day.distance * 100) / 100);
      this.fuel.data.push(Math.round(day.fuel * 100) / 100);
      this.numJobs.data.push(day.numJobs);
    }
    this.data = [this.distance, this.fuel, this.numJobs];

    this.chartOptions = {
      chart: {
        type: 'area',
      },
      title: {
        text: '',
      },
      tooltip: {
        split: true,
        valueSuffix: '',
      },
      xAxis: {
        categories: [
          '1',
          '2',
          '3',
          '4',
          '5',
          '6',
          '7',
          '8',
          '9',
          '10',
          '11',
          '12',
          '13',
          '14',
          '15',
          '16',
          '17',
          '18',
          '19',
          '20',
          '21',
          '22',
          '23',
          '24',
          '25',
          '26',
          '27',
          '28',
          '29',
          '30',
          '31',
        ],
        tickmarkPlacement: 'on',
        title: {
          enabled: false,
        },
      },
      credits: {
        enabled: false,
      },
      exporting: {
        enabled: true,
      },
      series: this.data,
    };
  }

  updateFlag = false;
  handleUpdate() {
    this.distance.data = [];
    this.fuel.data = [];
    this.numJobs.data = [];

    if (this.role === 'DRIVER') {
      this.dashboardService
        .getDriverDashboard(this.selected)
        .subscribe((data) => {
          for (let day of data) {
            this.distance.data.push(day.distance);
            this.fuel.data.push(day.fuel);
            this.numJobs.data.push(day.numJobs);
          }
          this.data = [this.distance, this.fuel, this.numJobs];
          this.chartOptions.series = this.data;
          this.updateFlag = true;
        });
    } else {
      this.dashboardService
        .getManagerDashboard(this.selected)
        .subscribe((data) => {
          for (let day of data) {
            this.distance.data.push(day.distance);
            this.fuel.data.push(day.fuel);
            this.numJobs.data.push(day.numJobs);
          }
          this.data = [this.distance, this.fuel, this.numJobs];
          this.chartOptions.series = this.data;
          this.updateFlag = true;
        });
    }
  }
}
