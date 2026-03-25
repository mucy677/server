# mvn clean package
docker build -t tileserver .
docker run -p 8000:8000 tileserver:latest