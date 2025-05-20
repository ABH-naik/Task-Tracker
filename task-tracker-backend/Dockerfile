FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file
COPY target/task-tracker-docker.jar task-tracker-docker.jar

# Expose the application port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "./task-tracker-docker.jar"]