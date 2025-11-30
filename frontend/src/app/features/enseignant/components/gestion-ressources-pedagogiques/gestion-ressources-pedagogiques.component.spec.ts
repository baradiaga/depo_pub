import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionRessourcesPedagogiquesComponent } from './gestion-ressources-pedagogiques.component';

describe('GestionRessourcesPedagogiquesComponent', () => {
  let component: GestionRessourcesPedagogiquesComponent;
  let fixture: ComponentFixture<GestionRessourcesPedagogiquesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionRessourcesPedagogiquesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionRessourcesPedagogiquesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
