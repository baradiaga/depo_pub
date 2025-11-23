import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionContenuComponent } from './gestion-contenu.component';

describe('GestionContenuComponent', () => {
  let component: GestionContenuComponent;
  let fixture: ComponentFixture<GestionContenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [GestionContenuComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(GestionContenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
