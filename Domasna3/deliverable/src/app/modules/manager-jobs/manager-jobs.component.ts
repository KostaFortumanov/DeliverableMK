import { Component, OnInit } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { JobService } from 'src/app/services/job.service';

export interface Job {
  id: number;
  address: string;
  description: string;
  driver: string;
}

@Component({
  selector: 'app-manager-jobs',
  templateUrl: './manager-jobs.component.html',
  styleUrls: ['./manager-jobs.component.scss'],
})
export class ManagerJobsComponent implements OnInit {
  show = false;
  pageLoading = true;
  error = '';
  searchField = '';

  constructor(private jobService: JobService) {}

  notAssigned: Job[] = [];
  assigned: Job[] = [];
  completed: Job[] = [];

  filterNotAssigned: Job[] = [];
  filterAssigned: Job[] = [];
  filterCompleted: Job[] = [];

  ngOnInit(): void {
    this.getUnassigned();
    this.getAssigned();
    this.getCompleted();
  }

  filter() {

    this.filterNotAssigned =  this.notAssigned.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchField.toLowerCase());
    });

    this.filterAssigned =  this.assigned.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchField.toLowerCase());
    });

    this.filterCompleted =  this.completed.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchField.toLowerCase());
    });
  }

  deleteJob(id: number) {
    this.jobService.deleteJob(id).subscribe((data) => {
      this.getUnassigned();
    });
  }

  getUnassigned() {
    this.jobService
      .getUnassigned()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.notAssigned = data;
          this.filterNotAssigned = data;
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }

  getAssigned() {
    this.jobService
      .getAssigned()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.assigned = data;
          this.filterAssigned = data;
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }

  getCompleted() {
    this.jobService
      .getCompleted()
      .pipe(
        finalize(() => {
          this.pageLoading = false;
          this.show = true;
        })
      )
      .subscribe(
        (data) => {
          this.completed = data;
          this.filterCompleted = data;
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }
}
