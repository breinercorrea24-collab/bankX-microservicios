package com.bca.core_banking_service.infrastructure.output.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import com.bca.core_banking_service.domain.ports.output.EventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Repository
public class KafkaProducerAdapter implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "account-events";

    @Override
    public void publish(Object event) {

        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(TOPIC, payload);

        } catch (Exception e) {
            throw new RuntimeException("Error publicando evento Kafka", e);
        }
    }
}
