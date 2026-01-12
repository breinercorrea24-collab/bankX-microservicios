package com.bca.reports_service.infrastructure.input.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bca.reports_service.application.ports.input.event.AccountDepositCommand;
import com.bca.reports_service.application.ports.input.usecases.ProcessAccountDepositUseCase;
import com.bca.reports_service.infrastructure.input.messaging.dto.AccountDepositMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final ProcessAccountDepositUseCase useCase;

    @KafkaListener(
        topics = "account-events",
        groupId = "transaction-service"
    )
    public void consume(String message) {

        log.info("Received message from Kafka: {}", message);

        try {
            // 1️⃣ Kafka Message → DTO
            AccountDepositMessage event =
                    objectMapper.readValue(message, AccountDepositMessage.class);

            log.debug("Parsed AccountDepositMessage: {}", event);

            // 2️⃣ DTO → Command
            AccountDepositCommand command = new AccountDepositCommand(
                    event.getAccountId(),
                    event.getAmount(),
                    event.getBalance()
            );

            log.debug("Created AccountDepositCommand: {}", command);

            // 3️⃣ Llamar al Use Case
            useCase.process(command);

            log.info("Successfully processed AccountDepositCommand for accountId: {}", event.getAccountId());

        } catch (Exception e) {
            log.error("Error processing Kafka message: {}", message, e);
            throw new RuntimeException("Error procesando evento Kafka", e);
        }
    }
}