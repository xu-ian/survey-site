import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { getResponsesForSurvey } from '../api/api';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import Cookies from 'js-cookie';
import { Responses, Response } from '../interfaces/response';

@Component({
  selector: 'app-analysispage',
  standalone: true,
  imports: [ CommonModule, FormsModule],
  templateUrl: './analysispage.component.html',
  styleUrl: './analysispage.component.scss'
})
export class AnalysispageComponent {
  responses: Responses[] = [];
  exists: Boolean = false;
  constructor(private router: Router, private route: ActivatedRoute) {};

  //Loads responses into data structure
  ngOnInit() {
    getResponsesForSurvey(this.route.snapshot.params['id'], (data: Response[]) => {
      if(data.length > 0) {
        this.exists = true;
        for(let question of data[0].answers) {
          this.responses.push({ id: question.question.id.toString(), 
                             question: question.question.name.toString(), 
                             type: question.question.type.toString(),
                             additional: question.question.additionalData,
                             answers: []});
        }
      } else {
        return
      }
      for(let response of data) {
        for(let question of response.answers) {
          this.responses.find(x => {return x.id == question.question.id})?.answers.push(question.response.toString());
        }
      }
      //console.log(this.responses);
    });

  }

  goBack: Function = () => {
    this.router.navigateByUrl("/survey");
  }
}
