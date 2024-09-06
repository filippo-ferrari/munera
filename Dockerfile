# Use a Maven image with OpenJDK
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build

# Install Node.js (for Vaadin frontend tasks)
RUN apk add --no-cache nodejs npm

# Set the working directory
WORKDIR /app

# Copy the Maven POM file and source code
COPY pom.xml ./
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Use a JDK image to run the JAR
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/munera-1.0-SNAPSHOT.jar ./munera-1.0-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/munera-1.0-SNAPSHOT.jar"]