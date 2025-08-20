import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Movie } from '../models/movie.model';

@Injectable({
  providedIn: 'root'
})
export class MovieService {
  private apiUrl = `${environment.apiUrl}/api/movies`;

  constructor(private http: HttpClient) { }

  // Error handling method
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  // Get all movies with optional status filter
  getMovies(status?: string): Observable<Movie[]> {
    let url = this.apiUrl;
    if (status) {
      url += `?status=${status}`;
    }
    return this.http.get<Movie[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  // Get a single movie by ID
  getMovie(id: number): Observable<Movie> {
    return this.http.get<Movie>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Add a new movie
  addMovie(movie: Movie): Observable<Movie> {
    return this.http.post<Movie>(this.apiUrl, movie).pipe(
      catchError(this.handleError)
    );
  }

  // Update an existing movie
  updateMovie(id: number, movie: Movie): Observable<Movie> {
    return this.http.put<Movie>(`${this.apiUrl}/${id}`, movie).pipe(
      catchError(this.handleError)
    );
  }

  // Delete a movie
  deleteMovie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Get movies for wishlist
  getWishlist(): Observable<Movie[]> {
    return this.getMovies('WISHLIST');
  }

  // Get watched movies
  getWatched(): Observable<Movie[]> {
    return this.getMovies('WATCHED');
  }

  // Search for movies from external API
  searchMovies(query: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/tmdb/search?query=${encodeURIComponent(query)}`).pipe(
      catchError(this.handleError)
    );
  }

  // Get trending movies from external API
  getTrending(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/tmdb/trending`).pipe(
      catchError(this.handleError)
    );
  }

  // Like a movie
  likeMovie(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/like`, {}).pipe(
      catchError(this.handleError)
    );
  }

  // Unlike a movie
  unlikeMovie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/like`).pipe(
      catchError(this.handleError)
    );
  }

  // Add a comment to a movie
  addComment(id: number, content: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/comments`, { content }).pipe(
      catchError(this.handleError)
    );
  }

  // Get comments for a movie
  getComments(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/comments`).pipe(
      catchError(this.handleError)
    );
  }
}