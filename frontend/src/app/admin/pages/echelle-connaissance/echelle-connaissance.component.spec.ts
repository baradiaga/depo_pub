import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EchelleConnaissanceComponent } from './echelle-connaissance.component';

describe('EchelleConnaissanceComponent', () => {
  let component: EchelleConnaissanceComponent;
  let fixture: ComponentFixture<EchelleConnaissanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EchelleConnaissanceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EchelleConnaissanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
