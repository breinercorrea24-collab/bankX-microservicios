Write-Host "Descargando imágenes Kafka / Confluent..."
docker pull confluentinc/cp-zookeeper:5.4.0
docker pull confluentinc/cp-kafka:5.4.0
docker pull confluentinc/cp-schema-registry:5.4.0
docker pull confluentinc/cp-enterprise-control-center:5.4.0

Write-Host "Descargando MongoDB..."
docker pull mongo:7

Write-Host "Descargando Elastic Stack..."
docker pull docker.elastic.co/elasticsearch/elasticsearch:8.13.4
docker pull docker.elastic.co/kibana/kibana:8.13.4
docker pull docker.elastic.co/logstash/logstash:8.13.4

Write-Host "✔ Descarga completada"