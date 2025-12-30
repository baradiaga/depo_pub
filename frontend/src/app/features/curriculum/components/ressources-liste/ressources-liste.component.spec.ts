import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RessourcesListeComponent } from './ressources-liste.component';

describe('RessourcesListeComponent', () => {
  let component: RessourcesListeComponent;
  let fixture: ComponentFixture<RessourcesListeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RessourcesListeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RessourcesListeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
