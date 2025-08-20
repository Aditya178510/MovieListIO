export const environment = {
  production: true,
  apiUrl: '/api', // This will be proxied to the backend container in Docker
  tmdbApiKey: '' // TMDB API key is handled by the backend
};