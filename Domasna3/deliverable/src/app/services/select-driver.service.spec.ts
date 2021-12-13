import { TestBed } from '@angular/core/testing';

import { SelectDriverService } from './select-driver.service';

describe('SelectDriverService', () => {
  let service: SelectDriverService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SelectDriverService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
