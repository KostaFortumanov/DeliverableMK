import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { Job } from 'src/app/models/job';
import { JobService } from 'src/app/services/job.service';

@Component({
  selector: 'app-driver-jobs',
  templateUrl: './driver-jobs.component.html',
  styleUrls: ['./driver-jobs.component.scss'],
})
export class DriverJobsComponent implements OnInit {
  show = false;
  pageLoading = true;
  error = '';
  searchAssigned = '';
  searchCompleted = '';

  constructor(private jobService: JobService) {}

  assigned: Job[] = [];
  completed: Job[] = [];

  ngOnInit(): void {
    this.getAssigned();
    this.getCompleted();
  }

  filterAssigned() {
    return this.assigned.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchAssigned.toLowerCase());
    });
  }

  filterCompleted() {
    return this.completed.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchCompleted.toLowerCase());
    });
  }

  getAssigned() {
    this.jobService
      .getMyAssignedJobs()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.assigned = data;
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }

  getCompleted() {
    this.jobService
      .getMyCompletedJobs()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.completed = data;
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }
}
