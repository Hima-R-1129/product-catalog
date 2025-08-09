# Stage 1: Build the application using Maven
FROM maven:3.9.4-eclipse-temurin-17 as build

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and download dependencies first (to leverage Docker cache)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy the rest of the source code
COPY src ./src

# Build the project and package the jar
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/product-catalog-0.0.1-SNAPSHOT.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
