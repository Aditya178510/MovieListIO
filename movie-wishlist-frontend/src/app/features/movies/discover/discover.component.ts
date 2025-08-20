import { Component, OnInit } from '@angular/core';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-discover',
  templateUrl: './discover.component.html',
  styleUrls: ['./discover.component.css']
})
export class DiscoverComponent implements OnInit {
  searchQuery = '';
  searchResults: any[] = [];
  trendingMovies: any[] = [];
  loading = false;
  searchLoading = false;
  error = '';

  constructor(private movieService: MovieService) { }

  ngOnInit(): void {
    this.loadTrendingMovies();
  }

  loadTrendingMovies(): void {
    this.loading = true;
    this.movieService.getTrending().subscribe({
      next: (data) => {
        this.trendingMovies = data.results || [];
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load trending movies. Please try again later.';
        console.error('Error loading trending movies:', error);
        this.loading = false;
      }
    });
  }

  searchMovies(): void {
    if (!this.searchQuery.trim()) {
      return;
    }

    this.searchLoading = true;
    this.movieService.searchMovies(this.searchQuery).subscribe({
      next: (data) => {
        this.searchResults = data.results || [];
        this.searchLoading = false;
      },
      error: (error) => {
        this.error = 'Failed to search movies. Please try again later.';
        console.error('Error searching movies:', error);
        this.searchLoading = false;
      }
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.searchResults = [];
  }
}