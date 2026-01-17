package com.bca.core_banking_service.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;

class TransferDomainServiceTest {

    @Test
    void executeMovesFundsBetweenAccounts() {
        DummyAccount source = new DummyAccount(BigDecimal.valueOf(100));
        DummyAccount destination = new DummyAccount(BigDecimal.valueOf(25));
        TransferDomainService service = new TransferDomainService();

        service.execute(source, destination, BigDecimal.valueOf(40));

        assertEquals(BigDecimal.valueOf(60), source.getBalance());
        assertEquals(BigDecimal.valueOf(65), destination.getBalance());
    }

    private static class DummyAccount extends Account {
        DummyAccount(BigDecimal balance) {
            super("customer", "USD", ProductStatus.ACTIVE, balance);
            this.type = AccountType.SAVINGS;
        }

        @Override
        public boolean hasMaintenanceFee() {
            return false;
        }

        @Override
        public void validateCreation() {
            // not needed
        }
    }
}
