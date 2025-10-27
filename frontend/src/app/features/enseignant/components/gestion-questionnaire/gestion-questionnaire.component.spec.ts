import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionQuestionnaireComponent } from './gestion-questionnaire.component';

describe('GestionQuestionnaireComponent', () => {
  let component: GestionQuestionnaireComponent;
  let fixture: ComponentFixture<GestionQuestionnaireComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestionQuestionnaireComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestionQuestionnaireComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

