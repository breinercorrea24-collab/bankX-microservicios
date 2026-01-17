package com.bca.core_banking_service.domain.ports.output.event;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;

class AccountEventPublisherTest {

    @Test
    void publisherImplementationsReceiveEvents() {
        RecordingPublisher publisher = new RecordingPublisher();
        AccountDepositEvent deposit = new AccountDepositEvent("acc-1", BigDecimal.ONE, BigDecimal.TEN);
        AccountWithdrawalEvent withdrawal = new AccountWithdrawalEvent("acc-1", BigDecimal.ONE, BigDecimal.ZERO);

        publisher.publishDeposit(deposit);
        publisher.publishWithdraw(withdrawal);

        assertTrue(publisher.depositHandled);
        assertTrue(publisher.withdrawHandled);
    }

    private static class RecordingPublisher implements AccountEventPublisher {
        boolean depositHandled;
        boolean withdrawHandled;

        @Override
        public void publishDeposit(AccountDepositEvent event) {
            depositHandled = event != null;
        }

        @Override
        public void publishWithdraw(AccountWithdrawalEvent event) {
            withdrawHandled = event != null;
        }
    }
}
