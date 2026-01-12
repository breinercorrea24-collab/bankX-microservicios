## 🐳 Build de imágenes Docker

### Requisitos
- Docker instalado
- PowerShell (Windows)

### Construir todas las imágenes

Desde la raíz del proyecto:

```bash
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

```bash
.\build-images.ps1
```
## 🚀 Iniciar los microservicios

Para iniciar la base de datos, ejecuta el siguiente comando:
```bash
docker-compose -f docker-compose.yml up -d
```

### Eureka
http://localhost:8761/

### Kafka
http://localhost:9021/clusters

#### Kafka Producer
```bash
docker exec -it broker kafka-console-producer --bootstrap-server broker:29092 --topic account-events
```
- {"accountId":"1","amount":100}
- {"accountId":"2","amount":50}

#### Kafka Consumer
```bash
docker exec -it broker kafka-console-consumer --bootstrap-server broker:29092 --topic account-events
```


