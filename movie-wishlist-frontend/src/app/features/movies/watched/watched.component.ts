import { Component, OnInit } from '@angular/core';
import { Movie } from '../../../core/models/movie.model';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-watched',
  templateUrl: './watched.component.html',
  styleUrls: ['./watched.component.css']
})
export class WatchedComponent implements OnInit {
  movies: Movie[] = [];
  loading = false;
  error = '';

  constructor(private movieService: MovieService) { }

  ngOnInit(): void {
    this.loadWatchedMovies();
  }

  loadWatchedMovies(): void {
    this.loading = true;
    this.movieService.getWatched().subscribe({
      next: (movies) => {
        this.movies = movies;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load watched movies. Please try again later.';
        console.error('Error loading watched movies:', error);
        this.loading = false;
      }
    });
  }
}