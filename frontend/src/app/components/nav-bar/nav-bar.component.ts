import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../auth/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent implements OnInit {
  userName = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userName = this.authService.getUsername();
  }

  logout() {
    this.authService.logout();
  }

  isAuthenticated() {
    return this.authService.isLoggedIn();
  }

  goToGlobalGallery(): Promise<boolean> {
    return this.router.navigate(['/']);
  }

  goToPrivateGallery(): Promise<boolean> {
    return this.router.navigate(['/private-gallery']);
  }

  goToUserInfo(): Promise<boolean> {
    return this.router.navigate(['/user-info']);
  }
}
