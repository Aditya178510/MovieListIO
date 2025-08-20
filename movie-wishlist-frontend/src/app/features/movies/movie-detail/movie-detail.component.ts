import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Movie } from '../../../core/models/movie.model';
import { MovieService } from '../../../core/services/movie.service';

@Component({
  selector: 'app-movie-detail',
  templateUrl: './movie-detail.component.html',
  styleUrls: ['./movie-detail.component.css']
})
export class MovieDetailComponent implements OnInit {
  movie: Movie | null = null;
  comments: any[] = [];
  loading = false;
  commentLoading = false;
  error = '';
  commentForm!: FormGroup;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private movieService: MovieService,
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.commentForm = this.formBuilder.group({
      content: ['', [Validators.required, Validators.maxLength(500)]]
    });
    
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadMovie(id);
        this.loadComments(id);
      }
    });
  }

  loadMovie(id: number): void {
    this.loading = true;
    this.movieService.getMovie(id).subscribe({
      next: (movie) => {
        this.movie = movie;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load movie details. Please try again later.';
        console.error('Error loading movie:', error);
        this.loading = false;
      }
    });
  }

  loadComments(id: number): void {
    this.movieService.getComments(id).subscribe({
      next: (comments) => {
        this.comments = comments;
      },
      error: (error) => {
        console.error('Error loading comments:', error);
      }
    });
  }

  toggleLike(): void {
    if (!this.movie) return;
    
    if (this.movie.userLiked) {
      this.movieService.unlikeMovie(this.movie.id).subscribe({
        next: () => {
          if (this.movie) {
            this.movie.userLiked = false;
            this.movie.likesCount = (this.movie.likesCount || 0) - 1;
          }
        },
        error: (error) => {
          console.error('Error unliking movie:', error);
        }
      });
    } else {
      this.movieService.likeMovie(this.movie.id).subscribe({
        next: () => {
          if (this.movie) {
            this.movie.userLiked = true;
            this.movie.likesCount = (this.movie.likesCount || 0) + 1;
          }
        },
        error: (error) => {
          console.error('Error liking movie:', error);
        }
      });
    }
  }

  submitComment(): void {
    if (this.commentForm.invalid || !this.movie) return;
    
    this.commentLoading = true;
    const content = this.commentForm.get('content')?.value;
    
    this.movieService.addComment(this.movie.id, content).subscribe({
      next: (comment) => {
        this.comments.unshift(comment);
        this.commentForm.reset();
        this.commentLoading = false;
        if (this.movie) {
          this.movie.commentsCount = (this.movie.commentsCount || 0) + 1;
        }
      },
      error: (error) => {
        console.error('Error adding comment:', error);
        this.commentLoading = false;
      }
    });
  }

  changeStatus(status: Movie['status']): void {
    if (!this.movie) return;
    
    const updatedMovie: Movie = { ...this.movie, status };
    this.loading = true;
    this.movieService.updateMovie(this.movie.id, updatedMovie).subscribe({
      next: (movie) => {
        this.movie = movie;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error updating movie status:', error);
        this.error = 'Failed to update movie status. Please try again.';
        this.loading = false;
      }
    });
  }

  editMovie(): void {
    if (!this.movie) return;
    this.router.navigate(['/movies/edit', this.movie.id]);
  }

  deleteMovie(): void {
    if (!this.movie) return;
    
    if (confirm('Are you sure you want to delete this movie?')) {
      this.movieService.deleteMovie(this.movie.id).subscribe({
        next: () => {
          this.router.navigate(['/movies/wishlist']);
        },
        error: (error) => {
          console.error('Error deleting movie:', error);
        }
      });
    }
  }
}