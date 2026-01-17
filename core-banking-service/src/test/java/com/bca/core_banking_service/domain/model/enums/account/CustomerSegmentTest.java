package com.bca.core_banking_service.domain.model.enums.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CustomerSegmentTest {

    @Test
    void valuesExposeRetailAndCorporate() {
        assertEquals(CustomerSegment.RETAIL, CustomerSegment.valueOf("RETAIL"));
        assertEquals(CustomerSegment.CORPORATE, CustomerSegment.values()[1]);
        assertEquals(2, CustomerSegment.values().length);
    }
}
