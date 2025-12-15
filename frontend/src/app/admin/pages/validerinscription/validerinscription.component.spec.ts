import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValiderinscriptionComponent } from './validerinscription.component';

describe('ValiderinscriptionComponent', () => {
  let component: ValiderinscriptionComponent;
  let fixture: ComponentFixture<ValiderinscriptionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ValiderinscriptionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ValiderinscriptionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
