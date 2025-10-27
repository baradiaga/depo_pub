import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChapitreDetailComponent } from './chapitre-detail.component';

describe('ChapitreDetailComponent', () => {
  let component: ChapitreDetailComponent;
  let fixture: ComponentFixture<ChapitreDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChapitreDetailComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChapitreDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
