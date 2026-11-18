FROM gradle:8.14.0-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle* settings.gradle* ./
COPY gradle ./gradle

COPY src ./src
RUN gradle bootJar --no-daemon -x test


FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN apk update && apk upgrade && \
    apk add --no-cache curl && \
    addgroup -S udecgroup && adduser -S appuser -G udecgroup && \
    mkdir -p /app/logs && chown -R appuser:udecgroup /app && \
    rm -rf /var/cache/apk/*

COPY --from=build --chown=appuser:udecgroup /app/build/libs/*.jar app.jar

USER appuser

# Render (y otros PaaS) inyectan PORT; en local se usa 8081.
ENV PORT=8081
# El plan gratuito de Render tiene 512 MB: la JVM usa como máximo el 75 % de la memoria del contenedor.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC"

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
