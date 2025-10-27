import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatieresReprendreComponent } from './matieres-reprendre.component';

describe('MatieresReprendreComponent', () => {
  let component: MatieresReprendreComponent;
  let fixture: ComponentFixture<MatieresReprendreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MatieresReprendreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MatieresReprendreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
