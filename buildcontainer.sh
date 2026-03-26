#!/bin/bash
# Build script for QuestShaper Server Docker container
# 
# IMPORTANT: Ensure all frontend files are in src/main/resources/static/
# - index.html, script.js, style.css
# - assets/64x64/ (all PNG images)

echo "Building Maven project..."
mvn clean package -DskipTests

echo "Building Docker image..."
docker build -t tileserver:latest .

echo "Running container on http://localhost:8000"
docker run -p 8000:8000 tileserver:latest