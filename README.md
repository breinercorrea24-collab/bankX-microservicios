## 🐳 Build de imágenes Docker

### Requisitos
- Docker instalado
- PowerShell (Windows)

### Construir todas las imágenes

Desde la raíz del proyecto:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

```powershell
.\build-images.ps1
```
## 🚀 Iniciar los microservicios

Para iniciar la base de datos, ejecuta el siguiente comando:
```bash
docker-compose -f docker-compose.yml up -d
```