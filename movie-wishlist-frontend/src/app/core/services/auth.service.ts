import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject: BehaviorSubject<User | null>;
  public currentUser: Observable<User | null>;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {
    const storedUser = localStorage.getItem('currentUser');
    this.currentUserSubject = new BehaviorSubject<User | null>(storedUser ? JSON.parse(storedUser) : null);
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): User | null {
    return this.currentUserSubject.value;
  }

  login(username: string, password: string): Observable<User> {
    return this.http.post<any>(`${environment.apiUrl}/api/auth/signin`, { username, password })
      .pipe(
        map(response => {
          // Store user details and jwt token in local storage to keep user logged in between page refreshes
          const token = response.accessToken || response.token;
          if (!token) {
            throw new Error('Token not found in response');
          }
          
          try {
            const decodedToken = this.jwtHelper.decodeToken(token);
            
            const user: User = {
              id: decodedToken.id || decodedToken.userId,
              username: decodedToken.sub || decodedToken.username,
              email: decodedToken.email,
              token: token
            };
            
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
            return user;
          } catch (error) {
            console.error('Error decoding token:', error);
            throw new Error('Invalid token format');
          }
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(`${environment.apiUrl}/api/auth/signup`, { username, email, password })
      .pipe(
        catchError(error => {
          return throwError(() => error);
        })
      );
  }

  logout(): void {
    // Remove user from local storage and set current user to null
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  isLoggedIn(): boolean {
    const currentUser = this.currentUserValue;
    if (!currentUser || !currentUser.token) {
      return false;
    }
    
    // Check if the token is expired
    return !this.jwtHelper.isTokenExpired(currentUser.token);
  }
}