import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerJobsComponent } from './manager-jobs.component';

describe('ManagerJobsComponent', () => {
  let component: ManagerJobsComponent;
  let fixture: ComponentFixture<ManagerJobsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManagerJobsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManagerJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
