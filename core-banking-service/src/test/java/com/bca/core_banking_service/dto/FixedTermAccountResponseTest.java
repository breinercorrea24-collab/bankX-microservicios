package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class FixedTermAccountResponseTest {

    @Test
    void fluentSettersPopulateFields() {
        FixedTermAccountResponse response = sampleResponse();

        assertEquals("acc-1", response.getId());
        assertEquals("customer-1", response.getCustomerId());
        assertEquals(AccountType.FIXED_TERM, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(Float.valueOf(150f), response.getBalance());
        assertEquals(FixedTermAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(Integer.valueOf(15), response.getAllowedDay());
        assertEquals(BigDecimal.valueOf(4.5), response.getInterestRate());
        assertEquals(Boolean.TRUE, response.getMaintenanceFeeFree());
        assertEquals(Integer.valueOf(25), response.getAllowedMovementDay());
        assertEquals(Integer.valueOf(1), response.getMovementsThisMonth());
    }

    @Test
    void equalsAndHashCodeConsiderEveryField() {
        FixedTermAccountResponse first = sampleResponse();
        FixedTermAccountResponse second = sampleResponse();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setAllowedDay(20);
        assertNotEquals(first, second);
    }

    @Test
    void statusEnumFromValueValidatesInput() {
        assertEquals(FixedTermAccountResponse.StatusEnum.ACTIVE,
                FixedTermAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> FixedTermAccountResponse.StatusEnum.fromValue("UNKNOWN"));
    }

    private FixedTermAccountResponse sampleResponse() {
        return new FixedTermAccountResponse(
                "acc-1",
                "customer-1",
                AccountType.FIXED_TERM,
                "USD",
                150f,
                FixedTermAccountResponse.StatusEnum.ACTIVE)
                .allowedDay(15)
                .interestRate(BigDecimal.valueOf(4.5))
                .maintenanceFeeFree(true)
                .allowedMovementDay(25)
                .movementsThisMonth(1);
    }
}
