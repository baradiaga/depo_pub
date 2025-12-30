import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatieresListeComponent } from './matieres-liste.component';

describe('MatieresListeComponent', () => {
  let component: MatieresListeComponent;
  let fixture: ComponentFixture<MatieresListeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MatieresListeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MatieresListeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
