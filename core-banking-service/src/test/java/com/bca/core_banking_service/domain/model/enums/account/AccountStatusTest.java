package com.bca.core_banking_service.domain.model.enums.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccountStatusTest {

    @Test
    void valuesProvideStableConstants() {
        AccountStatus[] statuses = AccountStatus.values();
        assertEquals(2, statuses.length);
        assertEquals(AccountStatus.ACTIVE, statuses[0]);
        assertEquals(AccountStatus.INACTIVE, AccountStatus.valueOf("INACTIVE"));
    }
}
