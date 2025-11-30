import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BanqueQuestionGestionComponent } from './banque-question-gestion.component';

describe('BanqueQuestionGestionComponent', () => {
  let component: BanqueQuestionGestionComponent;
  let fixture: ComponentFixture<BanqueQuestionGestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BanqueQuestionGestionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BanqueQuestionGestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
