
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
    rm -rf /var/cache/apk/*


COPY --from=build --chown=appuser:udecgroup /app/build/libs/*.jar app.jar


USER appuser

EXPOSE 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/api/ofertas/estudiantes || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]