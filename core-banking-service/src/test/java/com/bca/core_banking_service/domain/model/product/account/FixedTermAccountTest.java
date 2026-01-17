package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
