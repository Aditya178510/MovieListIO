import { Component } from '@angular/core';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Movie Wishlist';
  get isLoggedIn(): boolean { return this.auth.isLoggedIn(); }

  constructor(private auth: AuthService) {}

  logout(): void {
    this.auth.logout();
  }
}