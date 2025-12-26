# Run script for Peças Por Código Backend

function Main {
    Write-Host "--- Peças Por Código: Local Setup ---" -ForegroundColor Cyan

    # 1. Environment Variables
    if (-not (Test-Path ".env")) {
        Write-Host "[!] .env file not found. Creating from .env.example..." -ForegroundColor Yellow
        Copy-Item ".env.example" ".env"
        Write-Host "[+] Created .env. Please review it if you need specific keys." -ForegroundColor Green
    }

    # 2. Infrastructure (Docker)
    Write-Host "[*] Starting infrastructure (Postgres, Redis, Monitoring)..." -ForegroundColor Cyan
    docker compose -f docker-compose.local.yml up -d
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Failed to start Docker containers." -ForegroundColor Red
        return
    }

    Write-Host "[+] Infrastructure is up and running!" -ForegroundColor Green
    Write-Host "    - Postgres: localhost:5432"
    Write-Host "    - Redis: localhost:6379"
    Write-Host "    - Grafana: http://localhost:3000"
    Write-Host "    - Prometheus: http://localhost:9090"

    # 3. Backend (Optional auto-start)
    $choice = Read-Host "`nDo you want to start the backend application now? (y/N)"
    if ($choice -eq 'y' -or $choice -eq 'Y') {
        Write-Host "[*] Starting Backend via Gradle..." -ForegroundColor Cyan
        ./gradlew bootRun
    } else {
        Write-Host "`n[i] You can start the backend later using: ./gradlew bootRun" -ForegroundColor Gray
        Write-Host "[i] Or run it via your favorite IDE (IntelliJ/VS Code) using the 'local' profile." -ForegroundColor Gray
    }
}

Main
