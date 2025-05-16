FROM openjdk:17
WORKDIR /app
EXPOSE 8080
ADD target/task-tracker-docker.jar task-tracker-docker.jar
ENTRYPOINT ["java", "-jar", "/task-tracker-docker.jar"]