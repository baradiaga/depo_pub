import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListeMatieresComponent } from './liste-matieres.component';

describe('ListeMatieresComponent', () => {
  let component: ListeMatieresComponent;
  let fixture: ComponentFixture<ListeMatieresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListeMatieresComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListeMatieresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
