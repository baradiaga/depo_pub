import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FeatureAssignmentComponent } from './feature-assignment.component';

describe('FeatureAssignmentComponent', () => {
  let component: FeatureAssignmentComponent;
  let fixture: ComponentFixture<FeatureAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FeatureAssignmentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FeatureAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
