import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverJobsComponent } from './driver-jobs.component';

describe('DriverJobsComponent', () => {
  let component: DriverJobsComponent;
  let fixture: ComponentFixture<DriverJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DriverJobsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DriverJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
