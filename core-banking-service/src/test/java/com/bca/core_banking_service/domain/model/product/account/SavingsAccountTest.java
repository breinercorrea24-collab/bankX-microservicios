package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bca.core_banking_service.application.usecases.validation.BusinessAccountExtension;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class SavingsAccountTest {

    @Test
    void validateMonthlyLimit_allowsWhenUnderLimit() {
        SavingsAccount account = createAccount();
        account.setCurrentTransactions(4);

        assertDoesNotThrow(account::validateMonthlyLimit);
    }

    @Test
    void validateMonthlyLimit_throwsWhenLimitReached() {
        SavingsAccount account = createAccount();
        account.setCurrentTransactions(5);

        assertThrows(RuntimeException.class, account::validateMonthlyLimit);
    }

    @Test
    void validateCreation_isNotYetImplemented() {
        SavingsAccount account = createAccount();

        assertThrows(UnsupportedOperationException.class, account::validateCreation);
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        SavingsAccount first = createAccount();
        SavingsAccount second = createAccount();
        second.setCreatedAt(first.getCreatedAt());
        second.setId(first.getId());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        SavingsAccount different = createAccount();
        different.setMaintenanceCommission(BigDecimal.TEN);
        assertNotEquals(first, different);
    }

    @Test
    void hasMaintenanceFeeReturnsFalse() {
        SavingsAccount account = createAccount();
        assertEquals(false, account.hasMaintenanceFee());
    }

    @Test
    void equalsHandlesNullFields() {
        SavingsAccount first = createAccount();
        first.setMaintenanceCommission(null);
        first.setId(null);
        SavingsAccount second = createAccount();
        second.setMaintenanceCommission(null);
        second.setId(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);

        second.setMaintenanceCommission(BigDecimal.ONE);
        assertNotEquals(first, second);
    }

    @Test
    void noArgsConstructorAllowsSettingFields() {
        SavingsAccount account = new SavingsAccount();
        account.setCustomerId("cust");
        account.setCurrency("EUR");
        account.setType(AccountType.SAVINGS);
        account.setMaxMonthlyTransactions(10);
        account.setMaintenanceCommission(BigDecimal.TEN);

        assertEquals("cust", account.getCustomerId());
        assertEquals(10, account.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.TEN, account.getMaintenanceCommission());
    }

    @Test
    void equalsIgnoresSuperclassFieldsBecauseCallSuperFalse() {
        SavingsAccount first = createAccount();
        SavingsAccount second = createAccount();

        // change superclass fields but keep subclass fields intact
        second.setBalance(BigDecimal.valueOf(999));
        second.setCurrency("EUR");
        second.setCustomerId("other-customer");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void businessExtension_canBeAttachedWhenBusiness() {
        SavingsAccount account = createAccount();
        account.setCustomerType(CustomerType.BUSINESS);
        BusinessAccountExtension ext = mock(BusinessAccountExtension.class);

        account.attachBusinessExtension(ext);

        assertEquals(ext, account.getBusinessData());
        verify(ext).validateBusinessRules();
    }

    @Test
    void businessExtension_throwsForNonBusinessCustomers() {
        SavingsAccount account = createAccount();
        account.setCustomerType(CustomerType.PERSONAL);
        BusinessAccountExtension ext = mock(BusinessAccountExtension.class);

        assertThrows(BusinessException.class, () -> account.attachBusinessExtension(ext));
        Mockito.verifyNoInteractions(ext);
    }

    private SavingsAccount createAccount() {
        SavingsAccount account = new SavingsAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.SAVINGS,
                5,
                BigDecimal.ONE,
                BigDecimal.valueOf(100));
        account.setCurrentTransactions(0);
        return account;
    }
}
