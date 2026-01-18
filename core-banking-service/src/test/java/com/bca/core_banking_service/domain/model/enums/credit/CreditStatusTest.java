package com.bca.core_banking_service.domain.model.enums.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CreditStatusTest {

    @Test
    void valuesExposeAllStatuses() {
        CreditStatus[] statuses = CreditStatus.values();
        assertEquals(3, statuses.length);
        assertEquals(CreditStatus.ACTIVE, statuses[0]);
        assertEquals(CreditStatus.PAID, statuses[1]);
        assertEquals(CreditStatus.DEFAULTED, statuses[2]);
    }

    @Test
    void valueOfReturnsExpectedEnum() {
        assertEquals(CreditStatus.ACTIVE, CreditStatus.valueOf("ACTIVE"));
        assertEquals(CreditStatus.PAID, CreditStatus.valueOf("PAID"));
        assertEquals(CreditStatus.DEFAULTED, CreditStatus.valueOf("DEFAULTED"));
    }

    @Test
    void valueOfWithInvalidNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> CreditStatus.valueOf("UNKNOWN"));
    }
}
