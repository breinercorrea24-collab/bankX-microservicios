package com.bca.core_banking_service.domain.model.product.account;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class CheckingAccountTest {

    @Test
    void chargeMaintenanceFeeSubtractsBalance() {
        CheckingAccount account = new CheckingAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.CHECKING,
                10,
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(100));

        account.chargeMaintenanceFee();

        assertEquals(BigDecimal.valueOf(95), account.getBalance());
    }

    @Test
    void validateCreation_isNotImplemented() {
        CheckingAccount account = new CheckingAccount(
                "customer-1",
                "USD",
                ProductStatus.ACTIVE,
                AccountType.CHECKING,
                5,
                BigDecimal.ONE,
                BigDecimal.TEN);

        assertThrows(UnsupportedOperationException.class, account::validateCreation);
    }
}
