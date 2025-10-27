import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestdeconnaissanceComponent } from './testdeconnaissance.component';

describe('TestdeconnaissanceComponent', () => {
  let component: TestdeconnaissanceComponent;
  let fixture: ComponentFixture<TestdeconnaissanceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestdeconnaissanceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestdeconnaissanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
