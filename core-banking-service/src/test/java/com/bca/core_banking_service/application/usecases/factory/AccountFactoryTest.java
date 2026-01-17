package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;

class AccountFactoryTest {

    @Test
    void createSavingsAccount() {
        Account account = AccountFactory.create(command(AccountType.SAVINGS));

        assertTrue(account instanceof SavingsAccount);
        assertAccountBasics(account, AccountType.SAVINGS);
        assertEquals(ProductStatus.ACTIVE, account.getStatus());
    }

    @Test
    void createCheckingAccount() {
        Account account = AccountFactory.create(command(AccountType.CHECKING));

        assertTrue(account instanceof CheckingAccount);
        assertAccountBasics(account, AccountType.CHECKING);
    }

    @Test
    void createFixedTermAccount() {
        Account account = AccountFactory.create(command(AccountType.FIXED_TERM));

        assertTrue(account instanceof FixedTermAccount);
        assertAccountBasics(account, AccountType.FIXED_TERM);
        FixedTermAccount fixedTerm = (FixedTermAccount) account;
        assertEquals(BigDecimal.valueOf(4.5), fixedTerm.getInterestRate());
        assertTrue(fixedTerm.isMaintenanceFeeFree());
    }

    @Test
    void createVipSavingsAccount() {
        Account account = AccountFactory.create(command(AccountType.VIP_SAVINGS));

        assertTrue(account instanceof VipSavingsAccount);
        assertAccountBasics(account, AccountType.VIP_SAVINGS);
        VipSavingsAccount vip = (VipSavingsAccount) account;
        assertEquals(new BigDecimal("5000"), vip.getMinimumDailyAverage());
    }

    @Test
    void createPymeCheckingAccount() {
        Account account = AccountFactory.create(command(AccountType.PYME_CHECKING));

        assertTrue(account instanceof PymeCheckingAccount);
        assertAccountBasics(account, AccountType.PYME_CHECKING);
        PymeCheckingAccount pyme = (PymeCheckingAccount) account;
        assertEquals(Integer.MAX_VALUE, pyme.getMaxMonthlyTransactions());
    }

    @Test
    void create_withNullTypeThrowsBusinessException() {
        CreateAccountCommand command = CreateAccountCommand.builder()
                .customerId("customer")
                .type(null)
                .currency("USD")
                .build();

        assertThrows(BusinessException.class, () -> AccountFactory.create(command));
    }

    private CreateAccountCommand command(AccountType type) {
        return CreateAccountCommand.builder()
                .customerId("customer-1")
                .type(type)
                .currency("USD")
                .build();
    }

    private void assertAccountBasics(Account account, AccountType expectedType) {
        assertEquals("customer-1", account.getCustomerId());
        assertEquals("USD", account.getCurrency());
        assertEquals(expectedType, account.getType());
    }
}
