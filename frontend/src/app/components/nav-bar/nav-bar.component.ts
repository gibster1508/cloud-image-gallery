import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../auth/auth.service";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent implements OnInit {
  userName = '';

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.userName = this.authService.getUsername();
  }

  logout() {
    this.authService.logout();
  }

  isAuthenticated() {
    return this.authService.isLoggedIn();
  }
}
