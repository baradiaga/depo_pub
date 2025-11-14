import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasserTestConnaissanceComponent } from './passer-test-connaissance.component';

describe('PasserTestConnaissanceComponent', () => {
  let component: PasserTestConnaissanceComponent;
  let fixture: ComponentFixture<PasserTestConnaissanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PasserTestConnaissanceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PasserTestConnaissanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
