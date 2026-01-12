package com.bca.core_banking_service.domain.ports.output.event;

import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;

public interface AccountEventPublisher {
    void publishDeposit(AccountDepositEvent event);
}