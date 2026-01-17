package com.bca.core_banking_service.domain.model.product.credit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;

class CreditTest {

    @Test
    void payReducesOutstandingBalance() {
        TestCredit credit = new TestCredit();
        credit.setOutstandingBalance(BigDecimal.valueOf(1000));

        credit.pay(BigDecimal.valueOf(300));

        assertEquals(BigDecimal.valueOf(700), credit.getOutstandingBalance());
    }

    @Test
    void payWithAmountGreaterThanBalanceThrows() {
        TestCredit credit = new TestCredit();
        credit.setOutstandingBalance(BigDecimal.valueOf(200));

        assertThrows(RuntimeException.class, () -> credit.pay(BigDecimal.valueOf(201)));
    }

    @Test
    void getAvailableCreditReturnsDifference() {
        TestCredit credit = new TestCredit();
        credit.setCreditLimit(BigDecimal.valueOf(1500));
        credit.setOutstandingBalance(BigDecimal.valueOf(600));

        assertEquals(BigDecimal.valueOf(900), credit.getAvailableCredit());
    }

    private static class TestCredit extends Credit {
        TestCredit() {
            super("cred-id", "cust-id", ProductStatus.ACTIVE);
            this.creditLimit = BigDecimal.ZERO;
            this.outstandingBalance = BigDecimal.ZERO;
        }

        @Override
        public void validateCreation() {
            // not needed
        }
    }
}
