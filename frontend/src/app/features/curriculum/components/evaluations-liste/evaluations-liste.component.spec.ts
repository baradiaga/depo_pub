import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluationsListeComponent } from './evaluations-liste.component';

describe('EvaluationsListeComponent', () => {
  let component: EvaluationsListeComponent;
  let fixture: ComponentFixture<EvaluationsListeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EvaluationsListeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EvaluationsListeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
