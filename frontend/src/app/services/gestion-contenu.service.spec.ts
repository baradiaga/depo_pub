import { TestBed } from '@angular/core/testing';

import { GestionContenuService } from './gestion-contenu.service';

describe('GestionContenuService', () => {
  let service: GestionContenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GestionContenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
