package com.bca.reports_service.infrastructure.input.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.bca.reports_service.application.ports.input.event.AccountDepositCommand;
import com.bca.reports_service.application.ports.input.usecases.ProcessAccountDepositUseCase;
import com.bca.reports_service.infrastructure.input.messaging.dto.AccountDepositMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

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

        try {
            // 1️⃣ Kafka Message → DTO
            AccountDepositMessage event =
                    objectMapper.readValue(message, AccountDepositMessage.class);

            // 2️⃣ DTO → Command
            AccountDepositCommand command = new AccountDepositCommand(
                    event.getAccountId(),
                    event.getAmount(),
                    event.getBalance()
            );

            // 3️⃣ Llamar al Use Case
            useCase.process(command);

        } catch (Exception e) {
            throw new RuntimeException("Error procesando evento Kafka", e);
        }
    }
}