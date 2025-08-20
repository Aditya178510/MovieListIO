export interface Movie {
  id: number;
  title: string;
  genre: string;
  releaseYear: number;
  runtime: number;
  posterUrl: string;
  status: 'WISHLIST' | 'WATCHED';
  rating?: number;
  review?: string;
  userId: number;
  username: string;
  likesCount: number;
  commentsCount: number;
  userLiked: boolean;
  createdAt?: string;
  updatedAt?: string;
}