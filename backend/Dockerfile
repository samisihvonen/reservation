# ============================================
# Multi-stage Docker build for Spring Boot Backend
# ============================================

# Stage 1: Build
FROM maven:3.9.12-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy pom.xml and download dependencies (cacheable layer)
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from builder
COPY --from=builder /build/target/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

USER spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -cp /app/app.jar org.springframework.boot.loader.PropertiesLauncher \
    -cp /app/app.jar \
    -Dspring.profiles.active=health \
    || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]