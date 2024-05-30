import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalysispageComponent } from './analysispage.component';

describe('AnalysispageComponent', () => {
  let component: AnalysispageComponent;
  let fixture: ComponentFixture<AnalysispageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalysispageComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnalysispageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
