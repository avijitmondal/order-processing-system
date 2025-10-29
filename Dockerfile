# Multi-stage Dockerfile for Order Processing System
# Stage 1: Build
FROM gradle:8.5-jdk21 AS builder

WORKDIR /home/gradle/project

# Copy Gradle files for dependency caching
COPY build.gradle settings.gradle ./
COPY gradle/ gradle/
COPY gradlew .
RUN chmod +x gradlew

# Pre-fetch dependencies for better layer caching
RUN ./gradlew --no-daemon dependencies || true

# Copy source code
COPY src/ src/

# Build application (skip tests for faster builds)
RUN ./gradlew --no-daemon bootJar -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre AS runtime

# Create non-root user
RUN groupadd --system spring && \
    useradd --system --create-home --shell /usr/sbin/nologin --gid spring spring

WORKDIR /app

# Copy JAR from build stage
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# Set ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970" \
    JWT_EXPIRATION="86400000"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
