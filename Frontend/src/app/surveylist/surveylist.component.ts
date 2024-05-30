import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Survey } from '../interfaces/survey';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { createSurvey, getAllSurveys, deleteSurvey } from '../api/api';
import Cookies from 'js-cookie';

@Component({
  selector: 'app-surveylist',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './surveylist.component.html',
  styleUrl: './surveylist.component.scss'
})
export class SurveylistComponent {

  constructor(private router: Router, private route: ActivatedRoute){
    route.params.subscribe(val => {
      if(Cookies.get('Username')) {
        getAllSurveys(Cookies.get('Username')!, this.setSurveyList);
      } else {
        this.router.navigateByUrl('/auth');
      }      
    })
  };

  ngOnInit() {
    if(Cookies.get('Username')) {
      getAllSurveys(Cookies.get('Username')!, this.setSurveyList);
    } else {
      this.router.navigateByUrl('/auth');
    }
  }

  name!: String;

  surveys: Survey[] = [];

  newSurvey: Function = (event: Event) => {
    event.preventDefault();
    createSurvey(this.name, this.addSurveyToList);
  };

  editSurveyNav: Function = (id: String) => {
    this.router.navigateByUrl('/survey/' + id);
  }

  responseSurveyNav: Function = (id: String) => {
    this.router.navigateByUrl('/survey/' + id + "/analysis");
  }

  deleteSurvey: Function = (id: String) => {
    deleteSurvey(id, () => {getAllSurveys(Cookies.get('Username')!,this.setSurveyList)});
  }

  addSurveyToList: Function = (s: Survey) => {
    this.surveys.push(s);
  }

  setSurveyList: Function = (s: Survey[]) => {
    this.surveys = s;
  }

  copyToClipboard: Function = (id: string) => {
    navigator.clipboard.writeText(id);
  }
}
