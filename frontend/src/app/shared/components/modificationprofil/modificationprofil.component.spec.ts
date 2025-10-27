import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificationprofilComponent } from './modificationprofil.component';

describe('ModificationprofilComponent', () => {
  let component: ModificationprofilComponent;
  let fixture: ComponentFixture<ModificationprofilComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModificationprofilComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModificationprofilComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
