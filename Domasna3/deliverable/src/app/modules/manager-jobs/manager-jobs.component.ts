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
  searchNotAssigned = '';
  searchAssigned = '';
  searchCompleted = '';

  constructor(private jobService: JobService) {}

  notAssigned: Job[] = [];
  assigned: Job[] = [];
  completed: Job[] = [];

  ngOnInit(): void {
    this.getUnassigned();
    this.getAssigned();
    this.getCompleted();
  }

  filterNotAssigned() {
    return this.notAssigned.filter((job) => {
      let all = job.id + ' ' + job.address + ' ' + job.description + job.driver;
      return all.toLowerCase().includes(this.searchNotAssigned.toLowerCase());
    });

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
        },
        (error) => {
          this.error = 'Server Unavailable';
        }
      );
  }
}
