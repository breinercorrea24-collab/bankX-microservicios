package com.bca.core_banking_service.infrastructure.output.messaging.kafka;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;
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
    void publishDeposit_sendsSerializedEventToTopic() throws Exception {
        AccountDepositEvent event = new AccountDepositEvent("acc-1", null, null);
        doReturn("{\"id\":\"acc-1\"}").when(objectMapper).writeValueAsString(event);
        doReturn(null).when(kafkaTemplate).send("account-events", "{\"id\":\"acc-1\"}");

        adapter.publishDeposit(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("account-events", "{\"id\":\"acc-1\"}");
    }

    @Test
    void publishDeposit_whenSerializationFails_throwsRuntimeException() throws Exception {
        AccountDepositEvent event = new AccountDepositEvent("acc-err", null, null);
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> adapter.publishDeposit(event));
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void publishWithdraw_sendsSerializedEventToTopic() throws Exception {
        AccountWithdrawalEvent event = new AccountWithdrawalEvent("acc-2", null, null);
        doReturn("{\"id\":\"acc-2\"}").when(objectMapper).writeValueAsString(event);
        doReturn(null).when(kafkaTemplate).send("account-events", "{\"id\":\"acc-2\"}");

        adapter.publishWithdraw(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send("account-events", "{\"id\":\"acc-2\"}");
    }

    @Test
    void publishWithdraw_whenSerializationFails_throwsRuntimeException() throws Exception {
        AccountWithdrawalEvent event = new AccountWithdrawalEvent("acc-err", null, null);
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> adapter.publishWithdraw(event));
        verify(kafkaTemplate, never()).send(any(), any());
    }
}
