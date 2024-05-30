import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { signUp, logIn } from '../api/api';
import { Router } from '@angular/router';
import Cookies from 'js-cookie';

enum pageStatus {
   Login,
   Signup 
}

@Component({
  selector: 'app-authpage',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './authpage.component.html',
  styleUrl: './authpage.component.scss'
})
export class AuthpageComponent {

  constructor(private router: Router){};

  signup: String = "nav-link";
  login: String = "nav-link active";
  status: pageStatus = pageStatus.Login;
  state: String = "Log In";
  errorExists: Boolean = false;
  errorMessage: String = "";

  setSignup: Function = () => {
    this.signup = "nav-link active";
    this.login = "nav-link";
    this.status = pageStatus.Signup;
    this.state = "Sign Up";
  }

  setLogin: Function = () => {
    this.signup = "nav-link";
    this.login = "nav-link active";
    this.status = pageStatus.Login;
    this.state = "Log In";
  }

  authenticate: Function = () => {
    let username: string = $("#username-input").val()?.toString()!;
    let password: string = $("#password-input").val()?.toString()!;
    if(this.status == pageStatus.Login) {
      logIn(username, password, (dat: String) => {
        console.log(Cookies.get());
        this.router.navigateByUrl("/survey");
      }, (error: string) => {
        this.errorExists = true;
        this.errorMessage = error;
      });
    } else {
      signUp(username, password, (dat: String) => {
        this.errorExists = false;
        this.setLogin();
      }, (error: String) => {
        this.errorExists = true;
        this.errorMessage = error;
      });
    }
  
  }
}
