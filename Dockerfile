# Use an official OpenJDK image as a base image
FROM eclipse-temurin:21

# Set the working directory inside the container
WORKDIR /server

# Copy the built jar file into the container
COPY target/server-0.0.1-SNAPSHOT.jar server.jar

# Expose the application port
EXPOSE 8000

# Command to run the application
ENTRYPOINT ["java", "-jar", "server.jar"]