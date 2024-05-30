import { ApplicationConfig } from '@angular/core';
import { provideRouter, Routes } from '@angular/router';
import { SurveypageComponent } from './surveypage/surveypage.component';
import { SurveylistComponent } from './surveylist/surveylist.component';
import { ResponsepageComponent } from './responsepage/responsepage.component';
import { ResponsehomeComponent } from './responsehome/responsehome.component';
import { AuthpageComponent } from './authpage/authpage.component';
import { AnalysispageComponent } from './analysispage/analysispage.component';

export const routes: Routes = [
  {path: '', redirectTo: 'auth', pathMatch: 'full'},
  {path: 'auth', component: AuthpageComponent},
  {path: 'survey', component: SurveylistComponent},
  {path: 'survey/:id', component: SurveypageComponent},
  {path: 'survey/:id/analysis', component: AnalysispageComponent},
  {path: 'response', component: ResponsehomeComponent},
  {path: 'response/:rid', component: ResponsepageComponent}
];

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes)]
};
