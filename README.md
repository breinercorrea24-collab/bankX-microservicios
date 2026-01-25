## 🐳 Build de imágenes Docker

### Requisitos
- Docker instalado
- PowerShell (Windows)

### Construir todas las imágenes

Desde la raíz del proyecto:

```bash
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Infraestructura
```bash
.\imagenes-infra.ps1
```

Microservicios
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

## ▶️ Tests

Para ejecutar los tests unitarios y generar el reporte de cobertura con JaCoCo:

```bash
mvn clean test
mvn jacoco:report
```

### 📊 Reporte de cobertura

Luego de ejecutar los comandos, el reporte estará disponible en:

```text
target/site/jacoco/index.html
```

## 📚 Documentación API (Swagger)

Una vez levantados los servicios, puedes acceder a la documentación Swagger:

* **Account Service**
  [http://localhost:8081/account-service/swagger-ui.html](http://localhost:8081/account-service/swagger-ui.html)

* **Cards Service**
  [http://localhost:8082/cards-service/swagger-ui.html](http://localhost:8082/cards-service/swagger-ui.html)
