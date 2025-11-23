import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VueCoursComponent } from './vue-cours.component';

describe('VueCoursComponent', () => {
  let component: VueCoursComponent;
  let fixture: ComponentFixture<VueCoursComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [VueCoursComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VueCoursComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
