# Lightweight Java 17 runtime base image
FROM eclipse-temurin:17-jre-alpine

# Set working directory inside container
WORKDIR /app

# Copy the packaged JAR file into the container
COPY target/task-management-system-0.0.1-SNAPSHOT.jar app.jar

# Expose HTTP port 8081
EXPOSE 8081

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
