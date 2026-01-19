package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void equalsHandlesNullFields() {
        CheckingAccount first = sampleAccount(BigDecimal.ONE);
        first.setMaintenanceCommission(null);
        first.setId(null);
        CheckingAccount second = sampleAccount(BigDecimal.ONE);
        second.setMaintenanceCommission(null);
        second.setId(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        second.setMaintenanceCommission(BigDecimal.ONE);
        assertNotEquals(first, second);
    }

    @Test
    void equalsIgnoresSuperclassFieldsDueToCallSuperFalse() {
        CheckingAccount first = sampleAccount(BigDecimal.valueOf(20));
        CheckingAccount second = sampleAccount(BigDecimal.valueOf(999)); // different balance (superclass field)
        second.setCurrency("EUR"); // superclass field
        second.setCustomerId("other-customer"); // superclass field

        // equals/hashCode rely on CheckingAccount fields only (callSuper = false)
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void toStringContainsKeyInformation() {
        CheckingAccount account = sampleAccount(BigDecimal.valueOf(25));
        String description = account.toString();
        assertTrue(description.contains("CheckingAccount"));
        assertTrue(description.contains("maintenanceCommission=5"));
    }

    @Test
    void noArgsConstructorAllowsSettingFields() {
        CheckingAccount account = new CheckingAccount();
        account.setCustomerId("cust");
        account.setCurrency("EUR");
        account.setMaintenanceCommission(BigDecimal.TEN);
        account.setMaxMonthlyTransactions(20);

        assertEquals("cust", account.getCustomerId());
        assertEquals(20, account.getMaxMonthlyTransactions());
        assertEquals(BigDecimal.TEN, account.getMaintenanceCommission());
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
