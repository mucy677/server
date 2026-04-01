QuestShaper Server - Spring Boot + Frontend Game

QUICK START:
  Local: ./mvnw clean package -DskipTests && java -jar target/server-0.0.1-SNAPSHOT.jar
  Then open: http://localhost:8000

IMPORTANT: Frontend files must be in src/main/resources/static/
  - index.html
  - script.js
  - style.css
  - assets/64x64/ (all PNG images)

For detailed setup instructions, see SETUP.md

DEPLOYMENT:
  1. Build: ./mvnw clean package -DskipTests
  2. Docker: docker build -t tileserver:latest . && docker run -p 8000:8000 tileserver:latest
  3. Kubernetes: kubectl apply -f nginx-deployment.yaml && kubectl apply -f nginx-service.yaml