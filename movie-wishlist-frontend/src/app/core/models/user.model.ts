export interface User {
  id: number;
  username: string;
  email: string;
  token?: string;
  favoriteGenre?: string;
  profilePictureUrl?: string;
  role?: string;
  moviesWatched?: number;
  totalWatchTime?: number;
  followersCount?: number;
  followingCount?: number;
  badges?: string[];
  isFollowed?: boolean;
}