import { TestBed } from '@angular/core/testing';

import { AllDriverDetailsService } from './all-driver-details.service';

describe('AllDriverDetailsService', () => {
  let service: AllDriverDetailsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AllDriverDetailsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
