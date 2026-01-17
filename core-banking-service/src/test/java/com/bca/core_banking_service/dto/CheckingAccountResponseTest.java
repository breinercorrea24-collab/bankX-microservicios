package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CheckingAccountResponseTest {

    @Test
    void builderLikeMethodsPopulateFields() {
        CheckingAccountResponse response = sampleResponse();

        assertEquals("acc-4", response.getId());
        assertEquals("customer-4", response.getCustomerId());
        assertEquals(AccountType.CHECKING, response.getType());
        assertEquals("EUR", response.getCurrency());
        assertEquals(Float.valueOf(400f), response.getBalance());
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(Integer.valueOf(8), response.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.valueOf(3), response.getMaintenanceCommission());
    }

    @Test
    void equalsAndHashCodeCompareAllFields() {
        CheckingAccountResponse first = sampleResponse();
        CheckingAccountResponse second = sampleResponse();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setMaintenanceCommission(BigDecimal.valueOf(10));
        assertNotEquals(first, second);
    }

    @Test
    void statusEnumFromValueValidatesInput() {
        assertEquals(CheckingAccountResponse.StatusEnum.ACTIVE,
                CheckingAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> CheckingAccountResponse.StatusEnum.fromValue("UNKNOWN"));
    }

    private CheckingAccountResponse sampleResponse() {
        return new CheckingAccountResponse(
                "acc-4",
                "customer-4",
                AccountType.CHECKING,
                "EUR",
                400f,
                CheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(8)
                .maintenanceCommission(BigDecimal.valueOf(3));
    }
}
