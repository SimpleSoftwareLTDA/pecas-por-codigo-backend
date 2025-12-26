![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/SimpleSoftwareLTDA/pecas-por-codigo-backend?utm_source=oss&utm_medium=github&utm_campaign=SimpleSoftwareLTDA%2Fpecas-por-codigo-backend&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

Backend para o Novo Peças Por Código

[Site Peças Por Código](https://www.pecasporcodigo.com.br/)

## Local Development

### Prerequisites
- **Java 25** (OpenJDK)
- **Docker** & Docker Compose
- **PowerShell** (for Windows users)

### Quick Start (Recommended)
Run the helper script to set up environment variables and start the infrastructure:
```powershell
./run.ps1
```
This script will:
1. Create a `.env` file if it doesn't exist.
2. Start PostgreSQL, Redis, and the Monitoring stack (Grafana, Prometheus, Tempo, OTEL) via Docker.
3. Offer to start the backend application via `./gradlew bootRun`.

### Manual Setup

1. **Environment Variables**:
   Copy `.env.example` to `.env` and fill in necessary keys.
   ```bash
   cp .env.example .env
   ```

2. **Infrastructure**:
   Start the supporting services:
   ```bash
   docker compose -f docker-compose.local.yml up -d
   ```

3. **Run Application**:
   Ensure you use the `local` profile:
   ```bash
   ./gradlew bootRun
   ```
   Or via IntelliJ IDEA: Add `-Dspring.profiles.active=local` to VM Options.

### Infrastructure Access
- **API**: [http://localhost:8080](http://localhost:8080)
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Grafana**: [http://localhost:3000](http://localhost:3000) (Anonymous Admin enabled)
- **Prometheus**: [http://localhost:9090](http://localhost:9090)

## Licença
-------

Este projeto está licenciado sob Business Source License 1.1 (BSL 1.1). Veja o arquivo `LICENSE` na raiz do repositório para o texto completo e os parâmetros.

**Resumo:**
- **Licenciador:** Pecas Por Codigo (ou colaboradores deste repositório)
- **Obra licenciada:** novo-pecas-online-backend
- **Change Date:** 2029-11-13
- **Change License:** GNU GPL v2.0 ou qualquer versão posterior

*Observação importante: até a data de mudança (Change Date), o uso em produção é limitado conforme os termos da BSL; o uso para desenvolvimento, testes e não-produção é permitido. Após a Change Date, o projeto passa a estar sob a licença indicada em "Change License".*
