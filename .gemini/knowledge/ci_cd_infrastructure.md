# CI/CD and Infrastructure

## 1. GitHub Actions

The project utilizes automated workflows for:

- **Testing:** Running `./gradlew test` on every PR.
- **Building:** Compiling and packaging the JAR using Java 25.
- **Deployment:** Deployment to Cloudflare/Hetzner (or specified provider).

## 2. Docker Cloud

- **Dockerfile:** Multi-stage build optimized for size. Uses `eclipse-temurin` or similar for Java 25.
- **Docker Compose:** orchestration for local development including PostgreSQL, Redis, and Grafana.

## 3. Cloudflare Worker

- **worker/**: Contains the Edge logic, primarily for handling frontend requests, caching (SWR), and potentially CORS proxying.
- **CORS Policy:** Cloudflare Workers are configured to handle Cross-Origin Resource Sharing for the Peca Online web interface.
