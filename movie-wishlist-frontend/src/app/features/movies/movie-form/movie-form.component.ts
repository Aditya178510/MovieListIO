import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Movie } from '../../../core/models/movie.model';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-movie-form',
  templateUrl: './movie-form.component.html',
  styleUrls: ['./movie-form.component.css']
})
export class MovieFormComponent implements OnInit {
  movieForm!: FormGroup;
  isEditMode = false;
  movieId?: number;
  loading = false;
  submitting = false;
  error = '';
  
  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private movieService: MovieService
  ) { }

  ngOnInit(): void {
    this.initForm();
    
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.isEditMode = true;
        this.movieId = +id;
        this.loadMovie(this.movieId);
      }
    });
  }

  initForm(): void {
    this.movieForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      genre: ['', [Validators.maxLength(100)]],
      releaseYear: [null, [Validators.min(1900), Validators.max(new Date().getFullYear() + 5)]],
      runtime: [null, [Validators.min(1), Validators.max(999)]],
      posterUrl: ['', [Validators.maxLength(255)]],
      status: ['WISHLIST', Validators.required],
      rating: [null, [Validators.min(1), Validators.max(10)]],
      review: ['', [Validators.maxLength(1000)]]
    });
  }

  loadMovie(id: number): void {
    this.loading = true;
    this.movieService.getMovie(id).subscribe({
      next: (movie) => {
        this.movieForm.patchValue({
          title: movie.title,
          genre: movie.genre,
          releaseYear: movie.releaseYear,
          runtime: movie.runtime,
          posterUrl: movie.posterUrl,
          status: movie.status,
          rating: movie.rating,
          review: movie.review
        });
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load movie. Please try again later.';
        console.error('Error loading movie:', error);
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.movieForm.invalid) {
      return;
    }

    this.submitting = true;
    const movieData: Movie = this.movieForm.value;

    if (this.isEditMode && this.movieId) {
      this.movieService.updateMovie(this.movieId, movieData).subscribe({
        next: () => {
          this.submitting = false;
          this.router.navigate(['/movies/detail', this.movieId]);
        },
        error: (error) => {
          this.error = 'Failed to update movie. Please try again later.';
          console.error('Error updating movie:', error);
          this.submitting = false;
        }
      });
    } else {
      this.movieService.addMovie(movieData).subscribe({
        next: (movie) => {
          this.submitting = false;
          this.router.navigate(['/movies/detail', movie.id]);
        },
        error: (error) => {
          this.error = 'Failed to add movie. Please try again later.';
          console.error('Error adding movie:', error);
          this.submitting = false;
        }
      });
    }
  }

  cancel(): void {
    if (this.isEditMode && this.movieId) {
      this.router.navigate(['/movies/detail', this.movieId]);
    } else {
      this.router.navigate(['/movies/wishlist']);
    }
  }
}