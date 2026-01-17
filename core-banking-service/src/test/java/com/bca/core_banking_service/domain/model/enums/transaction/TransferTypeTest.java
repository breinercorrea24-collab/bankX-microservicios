package com.bca.core_banking_service.domain.model.enums.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TransferTypeTest {

    @Test
    void valueOf_returnsMatchingEnum() {
        assertEquals(TransferType.OWN, TransferType.valueOf("OWN"));
        assertEquals(TransferType.THIRD_PARTY, TransferType.valueOf("THIRD_PARTY"));
    }

    @Test
    void valueOf_throwsForUnknownValue() {
        assertThrows(IllegalArgumentException.class, () -> TransferType.valueOf("UNKNOWN"));
    }
}
