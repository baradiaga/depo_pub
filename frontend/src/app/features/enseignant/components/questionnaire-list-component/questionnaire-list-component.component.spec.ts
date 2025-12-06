import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionnaireListComponentComponent } from './questionnaire-list-component.component';

describe('QuestionnaireListComponentComponent', () => {
  let component: QuestionnaireListComponentComponent;
  let fixture: ComponentFixture<QuestionnaireListComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [QuestionnaireListComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(QuestionnaireListComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
