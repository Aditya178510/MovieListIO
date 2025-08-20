#!/bin/bash
# Setup script for Movie Wishlist application

# Check if .env file exists, if not create it from template
if [ ! -f ".env" ]; then
    echo "Creating .env file from template..."
    cp .env.example .env
    echo "Please edit the .env file with your configuration values."
fi

# Check if docker-compose.override.yml exists for development
read -p "Are you setting up for development? (y/n): " IS_DEV
if [ "$IS_DEV" = "y" ]; then
    if [ ! -f "docker-compose.override.yml" ]; then
        echo "Creating docker-compose.override.yml for development..."
        cp docker-compose.override.yml.example docker-compose.override.yml
    fi
    
    # Start in development mode
    echo "Starting application in development mode..."
    docker-compose up
else
    # Start in production mode
    echo "Starting application in production mode..."
    docker-compose up -d
fi