import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectionTestConnaissanceComponent } from './selection-test-connaissance.component';

describe('SelectionTestConnaissanceComponent', () => {
  let component: SelectionTestConnaissanceComponent;
  let fixture: ComponentFixture<SelectionTestConnaissanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectionTestConnaissanceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SelectionTestConnaissanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
