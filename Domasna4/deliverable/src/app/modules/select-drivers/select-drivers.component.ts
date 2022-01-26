import { Component, OnInit } from '@angular/core';
import {
  CdkDragDrop,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { SelectDriverService } from 'src/app/services/select-driver.service';
import { finalize } from 'rxjs/operators';
import { JobService } from 'src/app/services/job.service';

export interface SelectDriver {
  id: number;
  fullName: string;
  available: boolean;
}

export interface Preview {
  assignedJobs: string;
  driversUsed: string;
  fuelCost: string;
  time: string;
}

export interface Driver {
  firstName: string;
  lastName: string;
  jobs: Job[];
}
export interface Job {
  id: number;
  address: string;
  description: string;
}

@Component({
  selector: 'app-select-drivers',
  templateUrl: './select-drivers.component.html',
  styleUrls: ['./select-drivers.component.scss'],
})
export class SelectDriversComponent implements OnInit {
  show = false;
  pageLoading = true;
  pageLoadError = '';

  error = '';
  success = '';
  loading = false;

  constructor(
    private selectDriverService: SelectDriverService,
    private jobService: JobService
  ) {}

  ngOnInit(): void {
    this.getDrivers();
  }

  drivers: SelectDriver[] = [];

  selected: SelectDriver[] = [];

  detailDrivers: Driver[] = [];

  fuelPreview: Preview = {
    assignedJobs: '',
    driversUsed: '',
    fuelCost: '',
    time: '',
  };
  timePreview: Preview = {
    assignedJobs: '',
    driversUsed: '',
    fuelCost: '',
    time: '',
  };
  fuelPreviewLoading = false;
  timePreviewLoading = false;
  noSelection = true;

  getDrivers() {
    this.selectDriverService
      .getDrivers()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.drivers = data;
        },
        (error) => {
          this.pageLoadError = 'Could not get drivers';
        }
      );
  }

  assignJobs() {
    let payload: number[] = [];
    this.selected.forEach((driver) => payload.push(driver.id));
    this.loading = true;
    this.success = '';
    this.error = '';
    this.jobService
      .assignJobs(payload)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(
        (data) => {
          this.success = data.message;
          this.getDrivers();
          this.selected = [];
        },
        (error) => {
          if (error.status == '0') this.error = 'Server unavailable';
          else this.error = error.error.message;
        }
      );
  }

  preview() {
    if (this.selected.length > 0) {
      this.error = '';
      this.success = '';
      this.timePreviewLoading = true;
      let payload: number[] = [];
      this.selected.forEach((driver) => payload.push(driver.id));
      this.jobService
        .getPreview(payload)
        .pipe(
          finalize(() => {
            this.timePreviewLoading = false;
          })
        )
        .subscribe(
          (data) => {
            this.noSelection = false;
            this.timePreview = data;
            this.detailDrivers = data.drivers;
            this.timePreviewLoading = false;
            this.error = '';
          },
          (error) => {
            this.noSelection = true;
            this.error = error.error.message;
          }
        );
    } else {
      this.noSelection = true;
    }
  }

  drop(event: CdkDragDrop<SelectDriver[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
    } else {
      transferArrayItem(
        event.previousContainer.data,
        event.container.data,
        event.previousIndex,
        event.currentIndex
      );
      this.preview();
    }
  }
}
