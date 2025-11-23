import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormChapitreComponent } from './form-chapitre.component';

describe('FormChapitreComponent', () => {
  let component: FormChapitreComponent;
  let fixture: ComponentFixture<FormChapitreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FormChapitreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FormChapitreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
