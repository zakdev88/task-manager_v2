# Multi-stage build for Spring Boot app
FROM gradle:8.4-jdk21-alpine as build

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle settings.gradle ./
COPY gradle gradle

# Download dependencies
RUN gradle build --no-daemon -x test || return 0

# Copy source code
COPY src ./src

# Build the application
RUN gradle clean build --no-daemon -x test

# Production stage
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]