# Project Setup Checklist for Collaborators

## What Changed
This project was updated to properly serve the game frontend (HTML/CSS/JS) alongside the Spring Boot REST API. Previously, the frontend files weren't being bundled into the application.

## ✅ Required File Locations

### Frontend Assets (REQUIRED)
These files MUST exist in `src/main/resources/static/` for the game to work:

```
src/main/resources/static/
├── index.html              ✅ Must have
├── script.js               ✅ Must have
├── style.css               ✅ Must have
└── assets/64x64/
    ├── brickwall.png       ✅ Must have
    ├── bridge.png          ✅ Must have
    ├── doorsclosed.png     ✅ Must have
    ├── doorsopened.png     ✅ Must have
    ├── dirt.png            ✅ Must have
    ├── flagstones.png      ✅ Must have
    ├── grass.png           ✅ Must have
    ├── pebbles.png         ✅ Must have
    ├── rocks1.png          ✅ Must have
    ├── rocks2.png          ✅ Must have
    ├── rocks3.png          ✅ Must have
    ├── rocks6.png          ✅ Must have
    ├── sand.png            ✅ Must have
    ├── stonewall.png       ✅ Must have
    ├── tree.png            ✅ Must have
    ├── waterwaves.png      ✅ Must have
    ├── woodenboards.png    ✅ Must have
    └── player1.png         ✅ Must have
```

### Java Source
```
src/main/java/com/tiles/server/
├── ServerApplication.java        ✅ Main app
├── MyController.java             ✅ REST endpoints
└── RequestData.java              ✅ Data model
```

### Configuration
```
src/main/resources/
├── application.properties         ✅ Sets port to 8000
└── static/                        ✅ Contains all frontend files
```

---

## Setup Steps for New Collaborators

### 1. Clone Repository
```bash
git clone <repo-url>
cd server
```

### 2. Verify File Structure
Check that all files listed above exist in their correct locations.
```bash
# Should see these files:
ls src/main/resources/static/
ls src/main/resources/static/assets/64x64/
```

### 3. Build Project
```bash
./mvnw clean package -DskipTests
```

Expected output: `Copying 30 resources from src\main\resources to target\classes`

### 4. Run on Localhost
```bash
java -jar target/server-0.0.1-SNAPSHOT.jar
```

Open browser: **http://localhost:8000**

### 5. Test API Endpoints
- `GET http://localhost:8000/` → Game page
- `GET http://localhost:8000/info` → `{"placeholder":"placeholder"}`
- `POST http://localhost:8000/test` → Echo server (send JSON)

---

## How It Works

### Spring Boot Static File Serving
Spring Boot automatically serves files from `src/main/resources/static/` at the root path (`/`).

- Request: `GET http://localhost:8000/`
- Response: `src/main/resources/static/index.html`

- Request: `GET http://localhost:8000/script.js`
- Response: `src/main/resources/static/script.js`

- Request: `GET http://localhost:8000/assets/64x64/grass.png`
- Response: `src/main/resources/static/assets/64x64/grass.png`

### Building the JAR
When you run `./mvnw clean package`:
1. Maven compiles Java sources
2. Maven copies `src/main/resources/static/` → `target/classes/static/`
3. Spring Boot packages everything into `target/server-0.0.1-SNAPSHOT.jar`
4. The JAR contains the static files embedded

### Docker Container
The Dockerfile copies the JAR and runs it. Since static files are inside the JAR, they're automatically served.

---

## Common Mistakes to Avoid

### ❌ Mistake 1: Editing Frontend in Wrong Location
```
❌ WRONG: Frontend/script.js (won't be included in build)
✅ CORRECT: src/main/resources/static/script.js
```

### ❌ Mistake 2: Forgetting to Rebuild After Adding Assets
```
# Add new PNG file to src/main/resources/static/assets/64x64/
# Must rebuild!
./mvnw clean package -DskipTests
java -jar target/server-0.0.1-SNAPSHOT.jar
```

### ❌ Mistake 3: Missing Asset Files
If game shows black squares:
- Verify all PNG files exist in `src/main/resources/static/assets/64x64/`
- Check file names match exactly (case-sensitive on Linux/Mac)
- Rebuild the project

### ❌ Mistake 4: Port 8000 Already in Use
Port conflicts prevent startup. Kill existing process:
```bash
# Windows
netstat -ano | findstr :8000
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8000
kill -9 <PID>
```

---

## Git Workflow

### Before Committing
1. Ensure all static files are properly located
2. Build: `./mvnw clean package -DskipTests`
3. Test locally: `java -jar target/server-0.0.1-SNAPSHOT.jar`
4. Open browser to verify

### Files to Commit
```
✅ src/main/resources/static/ (all files)
✅ src/main/java/ (Java source)
✅ pom.xml
✅ Dockerfile
✅ *.yaml (Kubernetes manifests)
✅ README.txt, SETUP.md, buildcontainer.sh

❌ target/ (generated, add to .gitignore)
❌ .jar files (generated)
```

### .gitignore (should include)
```
target/
*.jar
.DS_Store
*.class
```

---

## Deployment

### Local Docker
```bash
./buildcontainer.sh
# Opens container at http://localhost:8000
```

### Kubernetes
```bash
# Push to registry
docker tag tileserver:latest yourusername/tileserver:latest
docker push yourusername/tileserver:latest

# Update nginx-deployment.yaml with your image
# Then deploy:
kubectl apply -f nginx-deployment.yaml
kubectl apply -f nginx-service.yaml
```

---

## Questions?
See SETUP.md for detailed instructions.
