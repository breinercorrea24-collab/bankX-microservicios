package com.bca.core_banking_service.infrastructure.output.messaging.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.bca.core_banking_service.domain.ports.output.event.AccountEventPublisher;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerAdapter implements AccountEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "account-events";

    @Override
    public void publishDeposit(AccountDepositEvent event) {
        log.info("Publishing deposit event to Kafka: {}", event);
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            log.info("Successfully published event to topic {}: {}", TOPIC, message);
        } catch (Exception e) {
            log.error("Error publishing event to Kafka: {}", event, e);
            throw new RuntimeException("Error publicando evento Kafka", e);
        }
    }

    @Override
    public void publishWithdraw(AccountWithdrawalEvent event) {
        log.info("Publishing withdrawal event to Kafka: {}", event);
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, message);
            log.info("Successfully published event to topic {}: {}", TOPIC, message);
        } catch (Exception e) {
            log.error("Error publishing event to Kafka: {}", event, e);
            throw new RuntimeException("Error publicando evento Kafka", e);
        }
    }

}