package com.bca.core_banking_service.infrastructure.output.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class KafkaProducerAdapterTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private KafkaProducerAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new KafkaProducerAdapter(kafkaTemplate, objectMapper);
    }

    @Test
    void publishDeposit_serializesEventAndSendsToKafka() throws Exception {
        AccountDepositEvent event = new AccountDepositEvent("acc-1", BigDecimal.ONE, BigDecimal.TEN);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"event\":\"deposit\"}");
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send("account-events", "{\"event\":\"deposit\"}")).thenReturn(future);

        adapter.publishDeposit(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("account-events", "{\"event\":\"deposit\"}");
    }

    @Test
    void publishWithdraw_serializesEventAndSendsToKafka() throws Exception {
        AccountWithdrawalEvent event = new AccountWithdrawalEvent("acc-1", BigDecimal.ONE, BigDecimal.TEN);
        when(objectMapper.writeValueAsString(event)).thenReturn("{\"event\":\"withdraw\"}");
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send("account-events", "{\"event\":\"withdraw\"}")).thenReturn(future);

        adapter.publishWithdraw(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("account-events", "{\"event\":\"withdraw\"}");
    }

    @Test
    void publishDeposit_wrapsSerializationException() throws Exception {
        AccountDepositEvent event = new AccountDepositEvent("acc-1", BigDecimal.ONE, BigDecimal.TEN);
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("boom") {});

        assertThrows(RuntimeException.class, () -> adapter.publishDeposit(event));
    }

    @Test
    void publishWithdraw_wrapsSerializationException() throws Exception {
        AccountWithdrawalEvent event = new AccountWithdrawalEvent("acc-1", BigDecimal.ONE, BigDecimal.TEN);
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("boom") {});

        assertThrows(RuntimeException.class, () -> adapter.publishWithdraw(event));
    }
}
