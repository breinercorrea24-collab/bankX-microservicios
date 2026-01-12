# ==========================
# Lista de servicios
# ==========================
$services = @(
    "api-gateway",
    "config-server",
    "eureka-server",
    "bootcoin-service",
    "cards-service",
    "core-banking-service",
    "customers-service",
    "reports-service",
    "users-service",
    "wallets-service"
)

$basePath = Get-Location
$errors = @()   # aquí acumulamos errores

foreach ($service in $services) {

    Write-Host "==============================="
    Write-Host "🚀 Procesando: $service"
    Write-Host "==============================="

    $servicePath = "$basePath\$service"

    # ==========================
    # 1️⃣ Build Spring Boot
    # ==========================
    Push-Location $servicePath

    Write-Host "☕ Compilando Spring Boot... $service"
    mvn clean package -DskipTests

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error compilando $service"
        $errors += @{
            service = $service
            step    = "maven"
            message = "Falló mvn clean package"
        }
        Pop-Location
        continue
    }

    Pop-Location

    # ==========================
    # 2️⃣ Build Docker
    # ==========================
    Write-Host "🐳 Construyendo imagen Docker... $service"
    docker build -t $service $servicePath

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error construyendo Docker de $service"
        $errors += @{
            service = $service
            step    = "docker"
            message = "Falló docker build"
        }
        continue
    }

    Write-Host "✅ $service construido correctamente`n"
}