package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class VipSavingsAccountResponseTest {

    @Test
    void requiredConstructorAndFluentSettersWork() {
        VipSavingsAccountResponse response = sampleResponse();

        assertEquals("acc-2", response.getId());
        assertEquals("customer-2", response.getCustomerId());
        assertEquals(AccountType.VIP_SAVINGS, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(Float.valueOf(200f), response.getBalance());
        assertEquals(VipSavingsAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(Integer.valueOf(6), response.getMaxMonthlyTransactions());
        assertEquals(Integer.valueOf(1), response.getCurrentTransactions());
        assertEquals(BigDecimal.valueOf(2), response.getMaintenanceCommission());
        assertEquals(BigDecimal.valueOf(5000), response.getMinimumDailyAverage());
    }

    @Test
    void equalsAndHashCodeCompareAllFields() {
        VipSavingsAccountResponse first = sampleResponse();
        VipSavingsAccountResponse second = sampleResponse();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setMinimumDailyAverage(BigDecimal.valueOf(1000));
        assertNotEquals(first, second);
    }

    @Test
    void statusEnumFromValueValidatesInput() {
        assertEquals(VipSavingsAccountResponse.StatusEnum.ACTIVE,
                VipSavingsAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> VipSavingsAccountResponse.StatusEnum.fromValue("UNKNOWN"));
    }

    private VipSavingsAccountResponse sampleResponse() {
        return new VipSavingsAccountResponse(
                "acc-2",
                "customer-2",
                AccountType.VIP_SAVINGS,
                "USD",
                200f,
                VipSavingsAccountResponse.StatusEnum.ACTIVE,
                BigDecimal.valueOf(5000))
                .maxMonthlyTransactions(6)
                .currentTransactions(1)
                .maintenanceCommission(BigDecimal.valueOf(2));
    }
}
