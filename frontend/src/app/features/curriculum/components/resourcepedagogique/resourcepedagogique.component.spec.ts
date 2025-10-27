import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResourcepedagogiqueComponent } from './resourcepedagogique.component';

describe('ResourcepedagogiqueComponent', () => {
  let component: ResourcepedagogiqueComponent;
  let fixture: ComponentFixture<ResourcepedagogiqueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResourcepedagogiqueComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResourcepedagogiqueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
