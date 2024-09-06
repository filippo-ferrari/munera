FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file into the container
COPY munera-1.0-SNAPSHOT.jar /app/munera-1.0-SNAPSHOT.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/munera-1.0-SNAPSHOT.jar"]