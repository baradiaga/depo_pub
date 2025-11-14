import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeroulementTestComponent } from './deroulement-test.component';

describe('DeroulementTestComponent', () => {
  let component: DeroulementTestComponent;
  let fixture: ComponentFixture<DeroulementTestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeroulementTestComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DeroulementTestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
