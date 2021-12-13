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
  id: number,
  fullName: string
}

@Component({
  selector: 'app-select-drivers',
  templateUrl: './select-drivers.component.html',
  styleUrls: ['./select-drivers.component.scss'],
})
export class SelectDriversComponent implements OnInit {

  show = false;
  pageLoading = true;
  error = '';

  assignJobError = '';
  assignJobSuccess = '';
  loading = false;

  constructor(
    private selectDriverService: SelectDriverService,
    private jobService: JobService 
  ) {}

  ngOnInit(): void {

    this.selectDriverService.getDrivers()
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
          this.error = 'Could not get drivers'
        }
      )
  }

  drivers: SelectDriver[] = [];

  selected: SelectDriver[] = [];

  assignJobs() {
    console.log(this.selected);
    let payload: number[] = [];
    this.selected.forEach(driver => payload.push(driver.id));
    this.loading = true;
    this.assignJobSuccess = '';
    this.assignJobError = '';
    this.jobService.assignJobs(payload)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(
        (data) => {
          this.assignJobSuccess = data.message;
        },
        (error) => {
          if(error.status == '0')
            this.assignJobError = "Server unavailable"
          else
            this.assignJobError = error.error.message;
        }
      )

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
    }
  }
}
