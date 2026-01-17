package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class PymeCheckingAccountTest {

    @Test
    void inheritsCheckingAccountFields() {
        PymeCheckingAccount account = createAccount();

        assertEquals(AccountType.PYME_CHECKING, account.getType());
        assertEquals(100, account.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.TEN, account.getMaintenanceCommission());
        assertEquals(BigDecimal.valueOf(500), account.getBalance());
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        PymeCheckingAccount first = createAccount();
        PymeCheckingAccount second = createAccount();
        second.setCreatedAt(first.getCreatedAt());
        second.setId(first.getId());
        second.setBalance(first.getBalance());
        second.setMaintenanceCommission(first.getMaintenanceCommission());
        second.setMaxMonthlyTransactions(first.getMaxMonthlyTransactions());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    private PymeCheckingAccount createAccount() {
        return new PymeCheckingAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.PYME_CHECKING,
                100,
                BigDecimal.TEN,
                BigDecimal.valueOf(500));
    }
}
