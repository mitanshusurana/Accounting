#!/bin/bash

echo "🚀 Starting Enterprise ERP Deployment..."
echo "📦 Building and starting Docker containers..."

docker-compose up --build -d

echo "✅ Deployment triggered successfully!"
echo "📡 Backend will be available at http://localhost:8080"
echo "🌐 Frontend will be available at http://localhost"
echo "📝 Database is exposed on localhost:5432"
