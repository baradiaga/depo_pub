import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MesCoursComponentComponent } from './mes-cours-component.component';

describe('MesCoursComponentComponent', () => {
  let component: MesCoursComponentComponent;
  let fixture: ComponentFixture<MesCoursComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MesCoursComponentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MesCoursComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
