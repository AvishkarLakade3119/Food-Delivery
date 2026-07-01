FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring && \
    apk add --no-cache curl wget

USER spring:spring

COPY --chown=spring:spring target/*.jar app.jar

EXPOSE 8085

HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD curl -fs http://localhost:8085/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT exec java $JAVA_OPTS -jar /app/app.jar