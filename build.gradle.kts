import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    var kotlinVersion = "2.1.20"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("com.gorylenko.gradle-git-properties") version "2.5.3"

    id("io.spring.dependency-management") version "1.1.6"
    id("io.sentry.jvm.gradle") version "5.12.2"

    id("org.springframework.boot") version "3.3.5"

}

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext.set(true)

    org.set("simple-software-q0")
    projectName.set("java-spring-boot")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}

group = "org"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Web dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-batch")

    // Database dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.hibernate.validator:hibernate-validator")
    implementation("com.h2database:h2:2.3.232")
    runtimeOnly("org.postgresql:postgresql")
//    implementation("org.flywaydb:flyway-core:11.1.1")
//    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.1.1")

    // Core dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Email dependencies
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("jakarta.mail:jakarta.mail-api:2.1.3")
    implementation("com.sun.mail:jakarta.mail:2.0.1")

    implementation("org.apache.james:apache-mime4j-core:0.8.12")
    implementation("commons-fileupload:commons-fileupload:1.4")


    // Logback (Spring Boot's default logger with SLF4J)
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")

    implementation("org.jsoup:jsoup:1.18.3")

    implementation("io.github.openfeign:feign-okhttp")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Documentation dependencies
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Micrometer dependencies
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.6.0")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.4")
    }
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}
