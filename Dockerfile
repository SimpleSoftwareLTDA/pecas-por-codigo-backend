# Use Eclipse Temurin JDK 21 base image with a Gradle wrapper
FROM amazoncorretto:21-alpine as build

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew ./
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Ensure Gradle wrapper is executable and download dependencies
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon dependencies

# Copy the entire project and build the application
COPY . .
RUN ./gradlew --no-daemon --parallel --build-cache clean bootJar

# Production stage: use a minimal JRE image for running the app
FROM amazoncorretto:21-alpine as runtime

# Set working directory and copy over the built JAR file
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseStringDeduplication", "-Xms256m", "-Xmx512m", "-jar", "/app/app.jar"]
