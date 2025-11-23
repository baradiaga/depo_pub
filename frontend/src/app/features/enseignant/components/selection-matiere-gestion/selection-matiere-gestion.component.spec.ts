import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectionMatiereGestionComponent } from './selection-matiere-gestion.component';

describe('SelectionMatiereGestionComponent', () => {
  let component: SelectionMatiereGestionComponent;
  let fixture: ComponentFixture<SelectionMatiereGestionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectionMatiereGestionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectionMatiereGestionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
