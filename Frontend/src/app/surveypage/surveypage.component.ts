/* eslint-disable no-console */

import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Form, FormsModule } from '@angular/forms';
import { Question, InputType } from '../interfaces/question';
import { createQuestion, getSurvey, removeQuestion, swapQuestion, updateQuestion } from '../api/api';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';


@Component({
  selector: 'app-surveypage',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './surveypage.component.html',
  styleUrl: './surveypage.component.scss'
})

export class SurveypageComponent {

  constructor(private route: ActivatedRoute, private router: Router) {};

  id!: String; 

  questionTypes: InputType[] = [
    {value: "short", name: "Short Answer"},
    {value:"long", name:"Long Answer"},
    {value:"single", name: "Multiple Choice(single)"},
    {value:"multiple", name: "Multiple Choice(multiple)"},
    {value:"scale", name: "Rating Scale"}
  ];

  survey: Question[] = [];

  ngOnInit() {
    this.id = this.route.snapshot.params['id'];
    getSurvey(this.id, this.setSurvey);
    //TODO: Retrieve the survey from the database
  }

  //Updates the survey components whenever any change is made to the survey
  setSurvey: Function = (s: Question[]) => {
    //console.log(s);
    this.survey = s;
  }

  //Adds a new question to the survey
  addQuestion: Function = (event: Event): void => {
    createQuestion(this.id, (String)($("#question-name").val()), (String)($("#question-type").val()), this.setSurvey);
    (<HTMLFormElement>$("form")[0]).reset();
  }

  removeQuestion: Function = (qid: String): void => {
    removeQuestion(this.id, qid, this.setSurvey);
  }

  updateQuestion: Function = (question: Question): void => {
    updateQuestion(this.id, question, this.setSurvey);
  }

  swapQuestionUp: Function = (pos: number): void => {
    swapQuestion(this.id, pos, pos-1, this.setSurvey);
  }

  swapQuestionDown: Function = (pos: number): void => {
    swapQuestion(this.id, pos, pos+1, this.setSurvey);
  }
  
  isAdditionalData: Function = (type: String): Boolean => {
    return type == "Multiple Choice(single)" || type == "Multiple Choice(multiple)" || type == "Rating Scale";
  }

  //Toggles between editing the question and viewing the question
  swapEdit: Function = (q: Question): void => {
    if(q.qedit) {
      this.updateQuestion(q);
    }
    q.qedit = !q.qedit;
  }

  //Toggles between editing the type and viewing the type
  swapTEdit: Function = (q: Question): void => {
    if(q.tedit) {
      this.updateQuestion(q);
    }
    q.tedit = !q.tedit;
  }

  swapAEdit: Function = (q: Question): void => {
    if(q.aedit) {
      this.updateQuestion(q);
    }
    q.aedit = !q.aedit;
  }
  
  returnToMain: Function = (): void => {
    this.router.navigateByUrl("/survey");
  }

}
