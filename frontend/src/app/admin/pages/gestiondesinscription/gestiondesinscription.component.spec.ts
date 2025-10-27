import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestiondesinscriptionComponent } from './gestiondesinscription.component';

describe('GestiondesinscriptionComponent', () => {
  let component: GestiondesinscriptionComponent;
  let fixture: ComponentFixture<GestiondesinscriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestiondesinscriptionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestiondesinscriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
