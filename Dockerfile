# ---------- Stage 1: build ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache dependencies as their own layer; only invalidated when pom.xml changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Stage 2: runtime ----------
FROM eclipse-temurin:21-jre-alpine
LABEL org.opencontainers.image.source="https://github.com/Vaibhav2824/SentinelSCM" \
      org.opencontainers.image.description="SentinelSCM: supply chain vendor risk management" \
      org.opencontainers.image.licenses="MIT"

RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=build /workspace/target/sentinelscm.jar app.jar
USER app

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health | grep -q '"UP"' || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
