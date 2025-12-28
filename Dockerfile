# Build stage: Cache dependencies and build the JAR
FROM amazoncorretto:25-alpine AS build

RUN apk add --no-cache findutils

WORKDIR /app

# Copy Gradle wrapper and build files first to cache dependencies
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts gradle.properties settings.gradle.kts ./

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Pre-download dependencies (improves build speed on subsequent runs)
RUN ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src src
RUN ./gradlew bootJar --no-daemon --parallel --build-cache -x generateGitProperties

# Extract layers for efficient Docker caching
RUN mkdir -p build/extracted && \
    java -Djarmode=layertools -jar build/libs/*.jar extract --destination build/extracted

# Production stage
FROM amazoncorretto:25-alpine AS runtime

WORKDIR /app

# Copy extracted layers from build stage
COPY --from=build /app/build/extracted/dependencies/ ./
COPY --from=build /app/build/extracted/spring-boot-loader/ ./
COPY --from=build /app/build/extracted/snapshot-dependencies/ ./
COPY --from=build /app/build/extracted/application/ ./

# Expose port
EXPOSE 8080

# Optimized ENTRYPOINT for Spring Boot Layers
ENTRYPOINT ["java", \
    "-Xms1g", \
    "-Xmx1g", \
    "-XX:+UseContainerSupport", \
    "-XX:+UseStringDeduplication", \
    "-XX:+ExitOnOutOfMemoryError", \
    "org.springframework.boot.loader.launch.JarLauncher"]
