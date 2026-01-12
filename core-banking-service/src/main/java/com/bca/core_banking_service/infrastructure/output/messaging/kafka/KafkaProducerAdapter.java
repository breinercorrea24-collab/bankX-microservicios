package com.bca.core_banking_service.infrastructure.output.messaging.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bca.core_banking_service.domain.ports.output.event.AccountEventPublisher;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class KafkaProducerAdapter implements AccountEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "account-events";

    @Override
    public void publishDeposit(AccountDepositEvent event) {
        try {
            kafkaTemplate.send(
                TOPIC,
                objectMapper.writeValueAsString(event)
            );
        } catch (Exception e) {
            throw new RuntimeException("Error publicando evento Kafka", e);
        }
    }

}