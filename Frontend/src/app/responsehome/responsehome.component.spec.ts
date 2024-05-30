import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponsehomeComponent } from './responsehome.component';

describe('ResponsehomeComponent', () => {
  let component: ResponsehomeComponent;
  let fixture: ComponentFixture<ResponsehomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsehomeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResponsehomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
