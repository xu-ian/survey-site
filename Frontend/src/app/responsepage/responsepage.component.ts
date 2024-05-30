import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { getSurvey, getResponse, updateResponse } from '../api/api';
import { Router } from '@angular/router';
import { Question, QData, InputType } from '../interfaces/question';
import { Response } from '../interfaces/response';
import { ActivatedRoute } from '@angular/router';


@Component({
  selector: 'app-responsepage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './responsepage.component.html',
  styleUrl: './responsepage.component.scss'
})
export class ResponsepageComponent {

  id!: String;
  rid!: String;

  survey: QData[] = [];

  constructor(private route: ActivatedRoute, private router: Router) {};

  ngOnInit() {
    this.rid = this.route.snapshot.params['rid'];
    getResponse(this.rid, this.setSurvey);
    }

  //Only called on initialization
  setSurvey: Function = (response: Response[]) => {
    this.id = response[0].sid;
    this.survey = [];
    response[0].answers.forEach(
      answer => {
        this.survey.push({question: answer.question, answer: answer.response});
      }
    );
    this.survey.sort((a, b) => { return (a.question.position as number) - (b.question.position as number)});
  }

  //Retrieves the choices from additional
  getMultipleChoice: Function = (choiceString: String) => {
    
    let choices: String[] = choiceString.split(",");
    choices.forEach(choice => {
      choice.trim;
    });
    return choices;
  }

  //Sets values for multiple choice
  setValue: Function = (response: QData, answer: String) => {
    let values : String[] = response.answer.split(",");
    if(values.includes(answer)) {
      response.answer = values.filter(value => {return value != answer}).join(",");
    } else {
      values.push(answer);
      response.answer = values.join(",");
    }
  }

  getValue: Function = (response: QData, answer: String) => {
    let values: String[] = response.answer.split(",");
    return values.includes(answer);
  }

  //Retrieves the minimum from additional
  getMin: Function = (scale: String) => {
    return scale.split("-")[0];
  }

  //Retrieves the maximum from additional
  getMax: Function = (scale: String) => {
    return scale.split("-")[1];
  }

  //Completes the survey
  backToResponses: Function = () => {
    this.router.navigateByUrl("/response");
  }

  //Concludes your response and returns
  submitResponse: Function = (event: Event) => {
    updateResponse(this.id, this.rid, this.survey, this.backToResponses);
  }

}
