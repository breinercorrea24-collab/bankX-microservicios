package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class SavingsAccountResponseTest {

    @Test
    void fluentSettersPopulateFields() {
        SavingsAccountResponse response = sampleResponse();

        assertEquals("acc-3", response.getId());
        assertEquals("customer-3", response.getCustomerId());
        assertEquals(AccountType.SAVINGS, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(Float.valueOf(300f), response.getBalance());
        assertEquals(SavingsAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(Integer.valueOf(5), response.getMaxMonthlyTransactions());
        assertEquals(Integer.valueOf(2), response.getCurrentTransactions());
        assertEquals(BigDecimal.valueOf(1), response.getMaintenanceCommission());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        SavingsAccountResponse first = sampleResponse();
        SavingsAccountResponse second = sampleResponse();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setCurrentTransactions(4);
        assertNotEquals(first, second);
    }

    @Test
    void statusEnumFromValueRequiresValidName() {
        assertEquals(SavingsAccountResponse.StatusEnum.ACTIVE,
                SavingsAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> SavingsAccountResponse.StatusEnum.fromValue("UNKNOWN"));
    }

    private SavingsAccountResponse sampleResponse() {
        return new SavingsAccountResponse(
                "acc-3",
                "customer-3",
                AccountType.SAVINGS,
                "USD",
                300f,
                SavingsAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(5)
                .currentTransactions(2)
                .maintenanceCommission(BigDecimal.valueOf(1));
    }
}
