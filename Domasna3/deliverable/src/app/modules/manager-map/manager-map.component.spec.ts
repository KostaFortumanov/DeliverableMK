import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagerMapComponent } from './manager-map.component';

describe('ManagerMapComponent', () => {
  let component: ManagerMapComponent;
  let fixture: ComponentFixture<ManagerMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManagerMapComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ManagerMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
