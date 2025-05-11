FROM amazoncorretto:24-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts gradle.properties settings.gradle.kts ./

# Ensure Gradle wrapper is executable
RUN chmod +x ./gradlew

# Copy the entire project and build the application
COPY . .
RUN ./gradlew --no-daemon --parallel --build-cache clean bootJar

# Production stage: use a minimal JRE image for running the app
FROM amazoncorretto:24-alpine AS runtime

# Set working directory and copy over the built JAR file
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseStringDeduplication", "-Xms512m", "-Xmx750m", "-jar", "/app/app.jar"]
