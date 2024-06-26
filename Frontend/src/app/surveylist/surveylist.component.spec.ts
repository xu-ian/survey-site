import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SurveylistComponent } from './surveylist.component';

describe('SurveylistComponent', () => {
  let component: SurveylistComponent;
  let fixture: ComponentFixture<SurveylistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SurveylistComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SurveylistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
