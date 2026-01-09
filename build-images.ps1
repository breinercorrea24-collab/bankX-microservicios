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

$basePath = Get-Location
$errors = @()   # <-- aquí acumulamos errores

foreach ($service in $services) {

    Write-Host "==============================="
    Write-Host "🚀 Procesando: $service"
    Write-Host "==============================="

    $servicePath = "$basePath\$service"

    # ==========================
    # 1️⃣ Build Spring Boot
    # ==========================
    Push-Location $servicePath

    Write-Host "☕ Compilando Spring Boot..."
    mvn clean package -DskipTests

    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Error compilando $service"
        $errors += @{
            service = $service
            step    = "maven"
            message = "Falló mvn clean package"
        }
        Pop-Location
        continue   # sigue con el siguiente servicio
    }

    Pop-Location

    # ==========================
    # 2️⃣ Build Docker
    # ==========================
    Write-Host "🐳 Construyendo imagen Docker..."
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

# ==========================
# 📋 RESUMEN FINAL
# ==========================
Write-Host "`n================================"
Write-Host "📊 RESUMEN FINAL"
Write-Host "================================"

if ($errors.Count -eq 0) {
    Write-Host "🎉 Todo se construyó correctamente"
} else {
    Write-Host "❌ Se encontraron errores:`n"

    foreach ($err in $errors) {
        Write-Host "🔴 Servicio: $($err.service)"
        Write-Host "   Paso:     $($err.step)"
        Write-Host "   Detalle:  $($err.message)`n"
    }

    Write-Host "⚠ Total de errores: $($errors.Count)"
}