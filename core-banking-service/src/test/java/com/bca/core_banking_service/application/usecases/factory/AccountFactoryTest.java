package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.bca.core_banking_service.application.usecases.validation.BusinessAccountExtension;
import com.bca.core_banking_service.domain.exceptions.BusinessException;
import com.bca.core_banking_service.domain.model.enums.account.AccountType;
import com.bca.core_banking_service.domain.model.enums.account.CustomerType;
import com.bca.core_banking_service.domain.model.enums.product.ProductStatus;
import com.bca.core_banking_service.domain.model.product.account.Account;
import com.bca.core_banking_service.domain.model.product.account.CheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.FixedTermAccount;
import com.bca.core_banking_service.domain.model.product.account.PymeCheckingAccount;
import com.bca.core_banking_service.domain.model.product.account.SavingsAccount;
import com.bca.core_banking_service.domain.model.product.account.VipSavingsAccount;

class AccountFactoryTest {

    @Test
    void create_savingsAccount() {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-1", CustomerType.PERSONAL, AccountType.SAVINGS, "USD");

        Account account = AccountFactory.create(cmd);

        assertTrue(account instanceof SavingsAccount);
        assertEquals("cust-1", account.getCustomerId());
        assertEquals("USD", account.getCurrency());
        assertEquals(AccountType.SAVINGS, account.getType());
        assertEquals(ProductStatus.ACTIVE, account.getStatus());
    }

    @Test
    void create_checkingAccount() {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-2", CustomerType.PERSONAL, AccountType.CHECKING, "EUR");

        Account account = AccountFactory.create(cmd);

        assertTrue(account instanceof CheckingAccount);
        assertEquals(AccountType.CHECKING, account.getType());
        assertEquals("EUR", account.getCurrency());
    }

    @Test
    void create_fixedTermAccount() {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-3", CustomerType.PERSONAL, AccountType.FIXED_TERM, "PEN");

        Account account = AccountFactory.create(cmd);

        assertTrue(account instanceof FixedTermAccount);
        assertEquals(AccountType.FIXED_TERM, account.getType());
        assertEquals("PEN", account.getCurrency());
    }

    @Test
    void create_vipSavingsAccount() {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-4", CustomerType.PERSONAL, AccountType.VIP_SAVINGS, "USD");

        Account account = AccountFactory.create(cmd);

        assertTrue(account instanceof VipSavingsAccount);
        assertEquals(AccountType.VIP_SAVINGS, account.getType());
        assertEquals("USD", account.getCurrency());
    }

    @Test
    void create_pymeCheckingAccount() {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-5", CustomerType.PERSONAL, AccountType.PYME_CHECKING, "CLP");

        Account account = AccountFactory.create(cmd);

        assertTrue(account instanceof PymeCheckingAccount);
        assertEquals(AccountType.PYME_CHECKING, account.getType());
        assertEquals("CLP", account.getCurrency());
    }

    @Test
    void create_businessAccount_attachesBusinessExtension() {
        CreateAccountCommand cmd = CreateAccountCommand.builder()
                .customerId("cust-biz")
                .customerType(CustomerType.BUSINESS)
                .type(AccountType.CHECKING)
                .currency("USD")
                .holders(List.of("holder-1"))
                .authorizedSigners(List.of("signer-1"))
                .build();

        assertThrows(BusinessException.class, () -> AccountFactory.create(cmd));
    }
}
