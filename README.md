# Movie Wishlist Application

A full-stack application for managing your movie wishlist, tracking watched movies, discovering new films, and sharing with friends.

## Features

- User authentication and profile management
- Movie wishlist and watched list management
- Movie discovery through TMDB API integration
- Social features (likes, comments, following)
- Analytics dashboard for viewing movie statistics

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security with JWT
- Spring Data JPA
- MySQL Database
- Maven

### Frontend
- Angular
- TypeScript
- HTML/CSS
- Bootstrap

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- npm
- MySQL
- Docker and Docker Compose (for containerized deployment)

## Running the Application

### Development Mode

#### Backend

1. Navigate to the backend directory:
   ```
   cd movie-wishlist-backend
   ```

2. Run the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```
   The backend will be available at http://localhost:8080

#### Frontend

1. Navigate to the frontend directory:
   ```
   cd movie-wishlist-frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Run the Angular development server:
   ```
   ng serve
   ```
   The frontend will be available at http://localhost:4200

### Using Docker Compose

To run the entire application stack (frontend, backend, and database) using Docker:

1. Make sure Docker and Docker Compose are installed on your system

2. Copy the `.env.example` file to `.env` and update with your TMDB API key and JWT secret:
   ```
   cp .env.example .env
   ```

3. Edit the `.env` file with your actual API keys and secrets

4. Build and start the containers:
   ```
   docker-compose up -d
   ```

5. Access the application at http://localhost

## Configuration

### Backend

The backend configuration can be modified in `application.properties` or through environment variables:

- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation
- `TMDB_API_KEY`: API key for The Movie Database

### Frontend

The frontend configuration can be modified in the environment files:

- `environment.ts`: Development environment settings
- `environment.prod.ts`: Production environment settings

Update the `apiUrl` to point to your backend API endpoint.

## Deployment

The application includes Docker configuration for easy deployment:

- `movie-wishlist-backend/Dockerfile`: Backend container configuration
- `movie-wishlist-frontend/Dockerfile`: Frontend container configuration
- `docker-compose.yml`: Multi-container application setup
- `.env.example`: Template for environment variables
- `docker-compose.override.yml.example`: Template for development overrides

### Production Deployment

1. Set up your environment variables:
   ```
   cp .env.example .env
   ```
   Edit the `.env` file with your production values.

2. Deploy the application:
   ```
   docker-compose up -d
   ```

### Development Setup

1. Set up your environment variables:
   ```
   cp .env.example .env
   cp docker-compose.override.yml.example docker-compose.override.yml
   ```

2. Start the development environment:
   ```
   docker-compose up
   ```
   This will start the application with development settings and enable hot-reloading.

## License

This project is licensed under the MIT License - see the LICENSE file for details.