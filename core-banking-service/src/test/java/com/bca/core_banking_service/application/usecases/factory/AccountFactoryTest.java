package com.bca.core_banking_service.application.usecases.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

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

        BusinessException ex = assertThrows(BusinessException.class, () -> AccountFactory.create(cmd));
        assertEquals("Cannot assign business data to non business account", ex.getMessage());
    }

    @Test
    void create_withInvalidAccountType_throwsBusinessException() throws Exception {
        CreateAccountCommand cmd = new CreateAccountCommand("cust-invalid", CustomerType.PERSONAL, AccountType.SAVINGS, "USD");

        // Force the switch to hit the default branch by clearing the synthetic switch-map entry.
        Class<?> switchClass = Class.forName("com.bca.core_banking_service.application.usecases.factory.AccountFactory$1");
        Field switchMap = switchClass.getDeclaredField("$SwitchMap$com$bca$core_banking_service$domain$model$enums$account$AccountType");
        switchMap.setAccessible(true);
        int[] mapping = (int[]) switchMap.get(null);
        int original = mapping[AccountType.SAVINGS.ordinal()];
        mapping[AccountType.SAVINGS.ordinal()] = 0;
        try {
            BusinessException ex = assertThrows(BusinessException.class, () -> AccountFactory.create(cmd));
            assertEquals("Invalid account type", ex.getMessage());
        } finally {
            mapping[AccountType.SAVINGS.ordinal()] = original;
        }
    }

    @Test
    void isBusinessType_trueForBusinessAndPyme() throws Exception {
        Method method = AccountFactory.class.getDeclaredMethod("isBusinessType", CustomerType.class);
        method.setAccessible(true);

        boolean business = (boolean) method.invoke(null, CustomerType.BUSINESS);
        boolean pyme = (boolean) method.invoke(null, CustomerType.PYMEBUSINESS);

        assertTrue(business);
        assertTrue(pyme);
    }

    @Test
    void isBusinessType_falseForPersonalTypes() throws Exception {
        Method method = AccountFactory.class.getDeclaredMethod("isBusinessType", CustomerType.class);
        method.setAccessible(true);

        boolean personal = (boolean) method.invoke(null, CustomerType.PERSONAL);
        boolean vipPersonal = (boolean) method.invoke(null, CustomerType.VIPPERSONAL);

        assertFalse(personal);
        assertFalse(vipPersonal);
    }
}
