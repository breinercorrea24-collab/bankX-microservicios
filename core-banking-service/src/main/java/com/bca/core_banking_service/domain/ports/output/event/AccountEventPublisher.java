package com.bca.core_banking_service.domain.ports.output.event;

import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountDepositEvent;
import com.bca.core_banking_service.infrastructure.output.messaging.kafka.dto.AccountWithdrawalEvent;

public interface AccountEventPublisher {
    void publishDeposit(AccountDepositEvent event);
    void publishWithdraw(AccountWithdrawalEvent event);

}