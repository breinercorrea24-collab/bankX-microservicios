# Lista de servicios
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

# Ruta base del proyecto
$basePath = Get-Location

foreach ($service in $services) {

    Write-Host "==============================="
    Write-Host "Construyendo Spring Boot: $service"
    Write-Host "==============================="

    $servicePath = "$basePath\$service"

    # 1️⃣ Compilar Spring Boot
    Push-Location $servicePath

    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error compilando $service. Abortando."
        exit 1
    }

    Pop-Location

    # 2️⃣ Construir imagen Docker
    Write-Host "🐳 Construyendo imagen Docker para $service..."
    docker build -t $service $servicePath

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error construyendo la imagen Docker de $service"
        exit 1
    }
}

Write-Host "✅ Todas las aplicaciones y las imágenes Docker se construyeron correctamente."