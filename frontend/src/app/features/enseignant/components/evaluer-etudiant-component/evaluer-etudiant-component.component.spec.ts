import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluerEtudiantComponentComponent } from './evaluer-etudiant-component.component';

describe('EvaluerEtudiantComponentComponent', () => {
  let component: EvaluerEtudiantComponentComponent;
  let fixture: ComponentFixture<EvaluerEtudiantComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EvaluerEtudiantComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EvaluerEtudiantComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
