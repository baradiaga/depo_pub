import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificationmotdepassComponent } from './modificationmotdepass.component';

describe('ModificationmotdepassComponent', () => {
  let component: ModificationmotdepassComponent;
  let fixture: ComponentFixture<ModificationmotdepassComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ModificationmotdepassComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModificationmotdepassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
