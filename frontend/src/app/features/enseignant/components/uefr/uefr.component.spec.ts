import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UefrComponent } from './uefr.component';

describe('UefrComponent', () => {
  let component: UefrComponent;
  let fixture: ComponentFixture<UefrComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UefrComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UefrComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
