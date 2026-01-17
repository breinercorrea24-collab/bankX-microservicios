package com.bca.core_banking_service.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class PymeCheckingAccountResponseTest {

    @Test
    void fluentApiSetsFields() {
        PymeCheckingAccountResponse response = sampleResponse();

        assertEquals("acc-5", response.getId());
        assertEquals("customer-5", response.getCustomerId());
        assertEquals(AccountType.PYME_CHECKING, response.getType());
        assertEquals("USD", response.getCurrency());
        assertEquals(Float.valueOf(500f), response.getBalance());
        assertEquals(PymeCheckingAccountResponse.StatusEnum.ACTIVE, response.getStatus());
        assertEquals(Integer.valueOf(12), response.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.valueOf(1), response.getMaintenanceCommission());
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        PymeCheckingAccountResponse first = sampleResponse();
        PymeCheckingAccountResponse second = sampleResponse();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        second.setMaxMonthlyTransactions(24);
        assertNotEquals(first, second);
    }

    @Test
    void statusEnumValidatesInput() {
        assertEquals(PymeCheckingAccountResponse.StatusEnum.ACTIVE,
                PymeCheckingAccountResponse.StatusEnum.fromValue("ACTIVE"));
        assertThrows(IllegalArgumentException.class,
                () -> PymeCheckingAccountResponse.StatusEnum.fromValue("UNKNOWN"));
    }

    private PymeCheckingAccountResponse sampleResponse() {
        return new PymeCheckingAccountResponse(
                "acc-5",
                "customer-5",
                AccountType.PYME_CHECKING,
                "USD",
                500f,
                PymeCheckingAccountResponse.StatusEnum.ACTIVE)
                .maxMonthlyTransactions(12)
                .maintenanceCommission(BigDecimal.valueOf(1));
    }
}
