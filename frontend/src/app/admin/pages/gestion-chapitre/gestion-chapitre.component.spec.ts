import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionChapitreComponent } from './gestion-chapitre.component';

describe('GestionChapitreComponent', () => {
  let component: GestionChapitreComponent;
  let fixture: ComponentFixture<GestionChapitreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionChapitreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionChapitreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
