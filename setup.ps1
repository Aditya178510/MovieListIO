# Setup script for Movie Wishlist application

# Check if .env file exists, if not create it from template
if (-not (Test-Path -Path ".env")) {
    Write-Host "Creating .env file from template..."
    Copy-Item ".env.example" ".env"
    Write-Host "Please edit the .env file with your configuration values."
}

# Check if docker-compose.override.yml exists for development
$isDev = Read-Host "Are you setting up for development? (y/n)"
if ($isDev -eq "y") {
    if (-not (Test-Path -Path "docker-compose.override.yml")) {
        Write-Host "Creating docker-compose.override.yml for development..."
        Copy-Item "docker-compose.override.yml.example" "docker-compose.override.yml"
    }
    
    # Start in development mode
    Write-Host "Starting application in development mode..."
    docker-compose up
} else {
    # Start in production mode
    Write-Host "Starting application in production mode..."
    docker-compose up -d
}