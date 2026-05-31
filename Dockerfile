# Use a base image with Java 17
FROM eclipse-temurin:17-jre-focal

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged Spring Boot application JAR file into the container
# The JAR file is typically found in the 'target' directory after a 'mvn package' command
ARG JAR_FILE=target/global-live-class-booking-service-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Expose the port your Spring Boot application runs on (8080 by default)
EXPOSE 8080

# Command to run the application when the container starts
ENTRYPOINT ["java","-jar","app.jar"]
