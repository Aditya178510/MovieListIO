import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { MovieListComponent } from './movie-list/movie-list.component';
import { MovieDetailComponent } from './movie-detail/movie-detail.component';
import { MovieFormComponent } from './movie-form/movie-form.component';
import { WishlistComponent } from './wishlist/wishlist.component';
import { WatchedComponent } from './watched/watched.component';
import { DiscoverComponent } from './discover/discover.component';

const routes: Routes = [
  { path: 'wishlist', component: WishlistComponent },
  { path: 'watched', component: WatchedComponent },
  { path: 'discover', component: DiscoverComponent },
  { path: 'detail/:id', component: MovieDetailComponent },
  { path: 'add', component: MovieFormComponent },
  { path: 'edit/:id', component: MovieFormComponent }
];

@NgModule({
  declarations: [
    MovieListComponent,
    MovieDetailComponent,
    MovieFormComponent,
    WishlistComponent,
    WatchedComponent,
    DiscoverComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes)
  ]
})
export class MoviesModule { }