package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class FixedTermAccountTest {

    @Test
    void validateOperationDate_allowsOnAllowedDay() {
        FixedTermAccount account = createAccount();
        account.setAllowedDay(15);

        assertDoesNotThrow(() -> account.validateOperationDate(LocalDate.of(2024, 4, 15)));
    }

    @Test
    void validateOperationDate_throwsOnDifferentDay() {
        FixedTermAccount account = createAccount();
        account.setAllowedDay(10);

        assertThrows(RuntimeException.class, () -> account.validateOperationDate(LocalDate.of(2024, 4, 9)));
    }

    @Test
    void validateCreation_isNotImplemented() {
        FixedTermAccount account = createAccount();

        assertThrows(UnsupportedOperationException.class, account::validateCreation);
    }

    @Test
    void equalsAndHashCodeIncludeFields() {
        FixedTermAccount first = createAccount();
        FixedTermAccount second = createAccount();
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        FixedTermAccount different = createAccount();
        different.setAllowedDay(1);
        assertNotEquals(first, different);
    }

    @Test
    void equalsHandlesNullFields() {
        FixedTermAccount first = createAccount();
        first.setAllowedMovementDay(null);
        first.setMovementsThisMonth(null);
        first.setInterestRate(null);
        FixedTermAccount second = createAccount();
        second.setAllowedMovementDay(null);
        second.setMovementsThisMonth(null);
        second.setInterestRate(null);
        second.setCreatedAt(first.getCreatedAt());

        assertEquals(first, second);

        second.setInterestRate(BigDecimal.ONE);
        assertNotEquals(first, second);
    }

    @Test
    void toStringContainsKeyFields() {
        FixedTermAccount account = createAccount();
        account.setAllowedDay(10);
        String description = account.toString();
        assertTrue(description.contains("allowedDay=10"));
        assertTrue(description.contains("interestRate=4.5"));
    }

    @Test
    void equalsIgnoresSuperclassFieldsBecauseCallSuperFalse() {
        FixedTermAccount first = createAccount();
        FixedTermAccount second = createAccount();

        // mutate superclass fields (balance/currency/customerId) and keep subclass fields intact
        second.setBalance(BigDecimal.valueOf(9999));
        second.setCurrency("EUR");
        second.setCustomerId("other-customer");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void hasMaintenanceFee_returnsFalse() {
        FixedTermAccount account = createAccount();
        assertEquals(false, account.hasMaintenanceFee());
    }

    @Test
    void noArgsConstructorAllowsSettingFields() {
        FixedTermAccount account = new FixedTermAccount();
        account.setCustomerId("cust");
        account.setCurrency("EUR");
        account.setAllowedDay(10);
        account.setInterestRate(BigDecimal.TEN);
        account.setMaintenanceFeeFree(true);

        assertEquals("cust", account.getCustomerId());
        assertEquals("EUR", account.getCurrency());
        assertEquals(10, account.getAllowedDay());
        assertEquals(BigDecimal.TEN, account.getInterestRate());
        assertTrue(account.isMaintenanceFeeFree());
    }

    private FixedTermAccount createAccount() {
        return new FixedTermAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.FIXED_TERM,
                BigDecimal.valueOf(4.5),
                true,
                15,
                0,
                BigDecimal.valueOf(1000));
    }
}
