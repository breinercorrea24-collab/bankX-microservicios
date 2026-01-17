package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class CheckingAccountTest {

    @Test
    void chargeMaintenanceFeeSubtractsBalance() {
        CheckingAccount account = sampleAccount(BigDecimal.valueOf(100));

        account.chargeMaintenanceFee();

        assertEquals(BigDecimal.valueOf(95), account.getBalance());
    }

    @Test
    void validateCreation_isNotImplemented() {
        CheckingAccount account = sampleAccount(BigDecimal.TEN);
        assertThrows(UnsupportedOperationException.class, account::validateCreation);
    }

    @Test
    void unsupportedOperationsThrow() {
        CheckingAccount account = sampleAccount(BigDecimal.TEN);

        assertThrows(UnsupportedOperationException.class,
                () -> account.deposit(BigDecimal.ONE));
        assertThrows(UnsupportedOperationException.class,
                () -> account.withdraw(BigDecimal.ONE));
        assertThrows(UnsupportedOperationException.class, account::hasMaintenanceFee);
    }

    @Test
    void equalsAndHashCodeConsiderFields() {
        CheckingAccount first = sampleAccount(BigDecimal.valueOf(50));
        CheckingAccount second = sampleAccount(BigDecimal.valueOf(50));
        second.setCreatedAt(first.getCreatedAt());
        second.setId(first.getId());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        CheckingAccount different = sampleAccount(BigDecimal.valueOf(50));
        different.setMaintenanceCommission(BigDecimal.valueOf(10));
        assertNotEquals(first, different);
    }

    private CheckingAccount sampleAccount(BigDecimal balance) {
        return new CheckingAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.CHECKING,
                10,
                BigDecimal.valueOf(5),
                balance);
    }
}
