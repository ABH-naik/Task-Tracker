FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file
COPY target/task-tracker-docker.jar task-tracker-docker.jar

# Expose the application port
EXPOSE 8080

# Set environment variables (These will be overridden by `docker-compose.yml`)
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/tasktracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
ENV SPRING_DATASOURCE_USERNAME=root
ENV SPRING_DATASOURCE_PASSWORD=root

# Start the application
ENTRYPOINT ["java", "-jar", "./task-tracker-docker.jar"]