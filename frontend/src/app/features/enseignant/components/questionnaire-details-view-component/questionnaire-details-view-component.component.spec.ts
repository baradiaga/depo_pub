import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuestionnaireDetailsViewComponentComponent } from './questionnaire-details-view-component.component';

describe('QuestionnaireDetailsViewComponentComponent', () => {
  let component: QuestionnaireDetailsViewComponentComponent;
  let fixture: ComponentFixture<QuestionnaireDetailsViewComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [QuestionnaireDetailsViewComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(QuestionnaireDetailsViewComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
