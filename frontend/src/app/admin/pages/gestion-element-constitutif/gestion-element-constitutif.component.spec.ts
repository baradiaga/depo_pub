import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionElementConstitutifComponent } from './gestion-element-constitutif.component';

describe('GestionElementConstitutifComponent', () => {
  let component: GestionElementConstitutifComponent;
  let fixture: ComponentFixture<GestionElementConstitutifComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionElementConstitutifComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionElementConstitutifComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
