# Use a Maven image with OpenJDK 22 to build the JAR
FROM maven:3.9.4-openjdk-22 AS build

# Set the working directory
WORKDIR /app

# Copy the source code into the container
COPY . .

# Build the JAR file
RUN mvn clean package -DskipTests

# Use a lightweight image with OpenJDK 22 to run the JAR
FROM openjdk:22-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/munera-1.0-SNAPSHOT.jar /app/munera-1.0-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/munera-1.0-SNAPSHOT.jar"]
