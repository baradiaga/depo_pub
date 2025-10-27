import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametrageChapitreComponent } from './parametrage-chapitre.component';

describe('ParametrageChapitreComponent', () => {
  let component: ParametrageChapitreComponent;
  let fixture: ComponentFixture<ParametrageChapitreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParametrageChapitreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ParametrageChapitreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
