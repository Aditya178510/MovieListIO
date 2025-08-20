import { Component, OnInit } from '@angular/core';
import { Movie } from '../../../core/models/movie.model';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-wishlist',
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css']
})
export class WishlistComponent implements OnInit {
  movies: Movie[] = [];
  loading = false;
  error = '';

  constructor(private movieService: MovieService) { }

  ngOnInit(): void {
    this.loadWishlist();
  }

  loadWishlist(): void {
    this.loading = true;
    this.movieService.getWishlist().subscribe({
      next: (movies) => {
        this.movies = movies;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load wishlist. Please try again later.';
        console.error('Error loading wishlist:', error);
        this.loading = false;
      }
    });
  }
}