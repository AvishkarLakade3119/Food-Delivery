# ===== payment-service =====
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Security: non-root user + tools
RUN addgroup -S spring && adduser -S spring -G spring && \
    apk add --no-cache curl wget

USER spring:spring

# Copy fat JAR built by Maven
COPY --chown=spring:spring target/*.jar app.jar

EXPOSE 8084

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -fs http://localhost:8084/actuator/health || exit 1

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java \ -jar /app/app.jar"]