# Use a Maven image to build the JAR
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and the source code
COPY pom.xml ./
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use an OpenJDK image to run the JAR
FROM openjdk:22-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/munera-1.0-SNAPSHOT.jar /app/munera-1.0-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/munera-1.0-SNAPSHOT.jar"]
