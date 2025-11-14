import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResultatDiagnosticComponent } from './resultat-diagnostic.component';

describe('ResultatDiagnosticComponent', () => {
  let component: ResultatDiagnosticComponent;
  let fixture: ComponentFixture<ResultatDiagnosticComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResultatDiagnosticComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResultatDiagnosticComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
