import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExercicesListeComponent } from './exercices-liste.component';

describe('ExercicesListeComponent', () => {
  let component: ExercicesListeComponent;
  let fixture: ComponentFixture<ExercicesListeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExercicesListeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExercicesListeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
