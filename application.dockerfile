# Use the official OpenJDK base image
FROM openjdk:17.0.1-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Boot JAR file into the container
COPY target/voucher-system-0.0.1-SNAPSHOT.jar /app/voucher-system.jar

# Expose the port that the Spring Boot app will run on
EXPOSE 8080

# Command to run the Spring Boot application
CMD ["java", "-jar", "voucher-system.jar"]
