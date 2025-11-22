# Multi-stage build for Config Server
FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy entire project to ensure all modules are available
COPY . .

# Build the specific module with all dependencies
RUN mvn clean package -pl config-server -am -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Add non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /build/config-server/target/*.jar app.jar

# Expose port
EXPOSE 8888

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8888/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
