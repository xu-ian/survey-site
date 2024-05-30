import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResponsepageComponent } from './responsepage.component';

describe('ResponsepageComponent', () => {
  let component: ResponsepageComponent;
  let fixture: ComponentFixture<ResponsepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResponsepageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ResponsepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
