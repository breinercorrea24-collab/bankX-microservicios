package com.bca.core_banking_service.domain.model.enums.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AccountTypeTest {

    @Test
    void fromStringRecognizesKnownValuesCaseInsensitive() {
        assertEquals(AccountType.CHECKING, AccountType.fromString("checking"));
        assertEquals(AccountType.VIP_SAVINGS, AccountType.fromString("VIP_SAVINGS"));
    }

    @Test
    void fromStringUnknownOrNullReturnsDefaultSavings() {
        assertEquals(AccountType.SAVINGS, AccountType.fromString("unknown-value"));
        assertEquals(AccountType.SAVINGS, AccountType.fromString(null));
    }
}
