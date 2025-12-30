import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprentissageCoursComponentComponent } from './apprentissage-cours-component.component';

describe('ApprentissageCoursComponentComponent', () => {
  let component: ApprentissageCoursComponentComponent;
  let fixture: ComponentFixture<ApprentissageCoursComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApprentissageCoursComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ApprentissageCoursComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
