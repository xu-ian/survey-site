import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { createResponse, getResponses, removeResponse } from '../api/api';
import { ActivatedRoute, Router } from '@angular/router';
import { Response } from '../interfaces/response';
import { QData } from '../interfaces/question';
import Cookies from 'js-cookie';

type dict<T> = {
  [key: string]: T;
}

@Component({
  selector: 'app-responsehome',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './responsehome.component.html',
  styleUrl: './responsehome.component.scss'
})
export class ResponsehomeComponent {

  responses: Response[] = [];

  error: Boolean = false;

  errorMessage: String = "";

  constructor(private router: Router, private route: ActivatedRoute){
    route.params.subscribe(val => {
      this.refresh();
    })
  };

  ngOnInit() {
    this.refresh();
  }

  setResponses: Function = (responses: Response[]) => {
    this.responses = responses;
    console.log(this.responses);
  }

  refresh: Function = () => {
    console.log(Cookies.get('Username'));
    if(Cookies.get('Username')) {
      getResponses(this.setResponses);
      this.error = false;
      this.errorMessage = "";
    } else {
      this.router.navigateByUrl("/auth");
    }

  }

  makeResponse: Function = (event: Event) => {
    event.preventDefault();
    createResponse((String)($("#response-id-input").val()), this.refresh, this.markError);
    console.log($("#response-id-input").val()?.toString());
  }

  //This is useless with the new implementation
  surveyButtonName: Function = (answers: QData[]) => {
    if(answers.length == 0) {
      return "Start Survey";
    } else {
      return "Edit Response";
    }
  }

  removeResponse: Function = (id: String) => {
    removeResponse(id, this.refresh);
  }

  startResponse: Function = (id: String) => {
    this.router.navigateByUrl('/response/' + id);
  }

  markError: Function = (error: String) => {
    this.error = true;
    //console.log(error);
    if(error == "Request failed with status code 500") {
      this.errorMessage = "This survey does not exist";
    } else {
      this.errorMessage = error;
    }
  }
}
