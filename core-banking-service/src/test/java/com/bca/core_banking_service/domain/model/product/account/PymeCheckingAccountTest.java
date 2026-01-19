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
    void equalsIgnoresSuperclassFieldsBecauseCallSuperFalse() {
        PymeCheckingAccount first = createAccount();
        PymeCheckingAccount second = createAccount();

        second.setBalance(BigDecimal.valueOf(999)); // superclass field
        second.setCurrency("EUR"); // superclass field
        second.setCustomerId("other-customer"); // superclass field

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
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

        PymeCheckingAccount different = createAccount();
        different.setMaintenanceCommission(BigDecimal.ZERO);
        // Because callSuper=false and no own fields, Lombok-generated equals ignores superclass fields
        assertEquals(first, different);
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
