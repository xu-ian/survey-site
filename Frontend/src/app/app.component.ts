import { Component } from '@angular/core';
import { RouterLinkActive, RouterLink, RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { logOut } from './api/api';
import Cookies from 'js-cookie';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  constructor(private router: Router) {};
  title = 'Frontend';

  logout: Function = () => {
    logOut(this.authpage);
  };

  authpage: Function = () => {
    this.router.navigateByUrl("/auth");
  };

  isLoggedIn: Function = () => {
    return Cookies.get('Username');
  }
}
